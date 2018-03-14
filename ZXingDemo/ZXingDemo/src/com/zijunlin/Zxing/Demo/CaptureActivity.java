package com.zijunlin.Zxing.Demo;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zijunlin.Zxing.Demo.camera.CameraManager;
import com.zijunlin.Zxing.Demo.decoding.CaptureActivityHandler;
import com.zijunlin.Zxing.Demo.decoding.InactivityTimer;
import com.zijunlin.Zxing.Demo.view.ViewfinderView;

public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private TextView txtResult;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	private Intent intent = new Intent();
	private Camera camera;
	private boolean hasStartPreview = false;
	private Parameters parameter;
	private Button btnCancel; // 取消
	private Button btnManual; // 手动输入
	private Button btnOpen; // 开灯
	private boolean isOpen = true; // 控制开关灯

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zxing);
		// 初始化 CameraManager
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		txtResult = (TextView) findViewById(R.id.txtResult);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		btnCancel = (Button) findViewById(R.id.cancel);
		btnCancel.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				CaptureActivity.this.finish();
				System.exit(0); 
			}
		});
		btnManual = (Button) findViewById(R.id.manual_input);
		btnOpen = (Button) findViewById(R.id.manual_open);
		btnOpen.setOnClickListener(openListener);
	}

	/**
	 * 开灯
	 */
	private OnClickListener openListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			camera = CameraManager.getCamera();
			parameter = camera.getParameters();
			// TODO 开灯
			if (isOpen) {		
				btnOpen.setText("关灯");
				parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(parameter);				
				isOpen = false;
			} else {  // 关灯
				btnOpen.setText("开灯");
				//parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
				parameter.setFlashMode(Parameters.FLASH_MODE_AUTO);
				camera.setParameters(parameter);
				isOpen = true;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	/**
	 * onPause比surfaceDestroyed() 先调用
	 */
	@Override
	protected void onPause() {
		super.onPause();

		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();

	}

	@Override
	protected void onDestroy() {		
		inactivityTimer.shutdown();	
//		if (camera != null) {
//			CameraManager.stopPreview();
//		}
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	/**
	 * 在surfaceCreated后调用，当surface发生变化也会触发该方法 这个方法一般至少被调用一次
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * surface只能够由一个线程操作，一旦被操作，其他线程就无法操作surface
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	/**
	 * 这里释放资源
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {		
		hasSurface = false;	    
		 if (camera != null) {
			CameraManager.stopPreview();
		 }
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		playBeepSoundAndVibrate();
		txtResult.setText(obj.getBarcodeFormat().toString() + ":"
				+ obj.getText());
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}