package com.example.dengtl.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button open_wifi, close_wifi;
    Button open_bt, close_bt;
    Button open_nfc;
    Button get_sms;
    Button call_number;
    Button get_volte;

    private BluetoothAdapter bluetoothAdapter;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tm = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        open_wifi = (Button) findViewById(R.id.open_wifi);
        close_wifi = (Button) findViewById(R.id.close_wifi);
        open_bt = (Button) findViewById(R.id.open_bt);
        close_bt = (Button) findViewById(R.id.close_bt);
        open_nfc = (Button) findViewById(R.id.open_nfc);
        get_sms = (Button) findViewById(R.id.get_sms);
        call_number =  (Button) findViewById(R.id.call_number);
        get_volte =  (Button) findViewById(R.id.get_volte_state);

        final WifiManager wm = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        open_wifi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (wm.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    wm.setWifiEnabled(true);
                }
            }
        });

        close_wifi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    wm.setWifiEnabled(false);
                }
            }
        });

        open_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean blueToothState = bluetoothAdapter.isEnabled();
                if (blueToothState) {
                    Toast.makeText(MainActivity.this, "蓝牙已经打开", Toast.LENGTH_SHORT).show();
                } else {
                    bluetoothAdapter.enable();
                    Toast.makeText(MainActivity.this, "正在打开..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        close_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean blueToothState = bluetoothAdapter.isEnabled();
                if (blueToothState) {
                    bluetoothAdapter.disable();
                    Toast.makeText(MainActivity.this, "正在关闭.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "蓝牙已经关闭!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        open_nfc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.settings.NFC_SETTINGS"));
                //startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        get_sms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getSmsFromPhone();
            }
        });

        call_number.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(MainActivity.this, "请授权拨打电话的功能！", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }else{
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                }else {
                    Intent intent=  new Intent(Intent.ACTION_CALL,Uri.parse("tel:400-8181800"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });

        get_volte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });


    }

    private Uri SMS_INBOX = Uri.parse("content://sms/");
    public void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[] {"_id", "address", "person","body", "date", "type" };
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("ooc","************cur == null");
            return;
        }
        while(cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
        }
    }


}
