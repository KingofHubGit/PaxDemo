package com.pax.telephonymanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import com.pax.telephonymanager.utils.DateUtil;
import com.pax.telephonymanager.utils.InfoUtil;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity
        extends Activity
        implements View.OnClickListener, DialogInterface.OnShowListener {

    private TelephonyManager mTelephonyManager;
    private BottomDialog mDialog;
    private ScrollView mScroll;
    private TextView mContent;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mContent.getText().length() > 10240) {
                    mContent.setText("");
                }
                mContent.append("\n" + msg.obj.toString() + "\n");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mScroll.fullScroll(View.FOCUS_DOWN);
                    }
                });
                
            }
        }
    };
    private File mFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();
        registPhoneStateListener();

    }


    private void initView() {
        if (mDialog == null) {
            mDialog = new BottomDialog(this);
        }
        mScroll = (ScrollView) findViewById(R.id.scrollView);
        mContent = (TextView) findViewById(R.id.content);
    }

    private void initData() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        createTxt();

    }

    private void initListener() {
        findViewById(R.id.getParam).setOnClickListener(this);
        mDialog.setOnShowListener(this);
    }

    private void createTxt() {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/PAX_" + InfoUtil.getMobileInfo() +
                "/telephonyTest/" + DateUtil.getDate()
                +"_TelephonyTest.txt";
        mFile = new File(filePath);
        File parentFile = mFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void registPhoneStateListener() {

        mTelephonyManager.listen(new PhoneStateListener() {

            public void onCallForwardingIndicatorChanged(boolean cfi) {
                writeLog2SD("onCallForwardingIndicatorChanged cfi=" + cfi);
            }

            public void onCallStateChanged(int state, String incomingNumber) {

                writeLog2SD("onCallStateChanged state=" + state
                        + getCallStateText(state) + " incomingNumberNumber="
                        + incomingNumber);
            }

            public void onCellInfoChanged(List<CellInfo> cellInfo) {
                String testMessage = "onCellInfoChanged: ";

                if (cellInfo == null) {
                    testMessage = testMessage + "cellInfo = null";
                } else {
                    testMessage = testMessage + "cellInfo.size()="
                            + cellInfo.size() + "\n";
                    for (int i = 0; i < cellInfo.size(); i++) {
                        testMessage = testMessage + "cellInfo[" + i + "]="
                                + cellInfo.get(i) + "\n";
                    }
                }

                writeLog2SD(testMessage);
            }

            public void onCellLocationChanged(CellLocation location) {
                writeLog2SD("onCellLocationChanged location=" + location);
            }

            public void onDataActivity(int direction) {

                writeLog2SD("onDataActivity direction=" + direction
                        + getDataActivityText(direction));
            }

            public void onDataConnectionStateChanged(int state) {

                writeLog2SD("onDataConnectionStateChanged state = " + state
                        + getDataConnectionStateText(state));
            }

            public void onDataConnectionStateChanged(int state, int networkType) {
                writeLog2SD("onDataConnectionStateChanged state=" + state
                        + getDataConnectionStateText(state) + " networkType="
                        + networkType + getNetworkTypeText(networkType));
            }

            public void onMessageWaitingIndicatorChanged(boolean mwi) {
                writeLog2SD("onMessageWaitingIndicatorChanged mwi = " + mwi);
            }

            public void onServiceStateChanged(ServiceState serviceState) {
                writeLog2SD("onServiceStateChanged serviceState = " + serviceState);
            }

            public void onSignalStrengthChanged(int asu) {
                writeLog2SD("onSignalStrengthChanged asu = " + asu);
            }

            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                writeLog2SD("onSignalStrengthsChanged signalStrength = "
                        + signalStrength);
            }

        }, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                | PhoneStateListener.LISTEN_CALL_STATE
                | PhoneStateListener.LISTEN_CELL_INFO
                | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_DATA_ACTIVITY
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
                | PhoneStateListener.LISTEN_SERVICE_STATE
                | PhoneStateListener.LISTEN_SIGNAL_STRENGTH
                | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onClick(View v) {

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        mDialog.show();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        mDialog.setInfo(getContent());
    }

    private String getContent() {
    	
    	StringBuilder testMessage = new StringBuilder(DateUtil.getDateN());
    	
		String baseVersion = null;
		try { 

			Class cl = Class.forName("android.os.SystemProperties"); 

			Object invoker = cl.newInstance(); 

			Method m = cl.getMethod("get", new Class[] { String.class,String.class }); 

			Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "Error"}); 

			baseVersion = (String)result;
			} catch (Exception e) {
				e.printStackTrace();

		}
		
		testMessage.append("GetBaseVersion:  "+baseVersion+"\n");
    	
    	
        
        List<CellInfo> cellInfo = mTelephonyManager.getAllCellInfo();
        if (cellInfo == null) {
            testMessage.append("GetAllCellInfo() = null\n");
        } else {
            testMessage.append("GetAllCellInfo().size() = " + cellInfo.size() + "\n");

            for (int i = 0; i < cellInfo.size(); i++) {
                testMessage.append("GetAllCellInfo()[" + i + "]= " + cellInfo.get(i) + "\n");
            }
        }

        List<NeighboringCellInfo> neighboring = mTelephonyManager.getNeighboringCellInfo();
        if (neighboring == null) {
            testMessage.append("GetNeighboringCellInfo() = null\n");
        } else {
            testMessage.append("GetNeighboringCellInfo().size() = " + neighboring.size() + "\n");
            for (int i = 0; i < neighboring.size(); i++) {
                testMessage.append(
                        "GetNeighboringCellInfo()[" + i + "]= " + neighboring.get(i) + "\n");
            }
        }

        testMessage.append(
                "GetCallState() = " + mTelephonyManager.getCallState() + getCallStateText(
                        mTelephonyManager.getCallState()) + "\n" +
                        "GetCellLocation() = " + mTelephonyManager.getCellLocation() + "\n" +
                        "GetDataActivity() = " + mTelephonyManager.getDataActivity() + getDataActivityText(
                        mTelephonyManager.getDataActivity()) + "\n" +
                        "GetDataState() = " + mTelephonyManager.getDataState() + getDataConnectionStateText(
                        mTelephonyManager.getDataState()) + "\n" +
                        "GetDeviceId() = " + mTelephonyManager.getDeviceId() + "\n" +
                        "GetDeviceSoftwareVersion() = " + mTelephonyManager.getDeviceSoftwareVersion() + "\n" +
                        "GetGroupIdLevel1() = " + mTelephonyManager.getGroupIdLevel1() + "\n" +
                        "GetLine1Number() = " + mTelephonyManager.getLine1Number() + "\n" +
                        "GetMmsUAProfUrl() = " + mTelephonyManager.getMmsUAProfUrl() + "\n" +
                        "GetMmsUserAgent() = " + mTelephonyManager.getMmsUserAgent() + "\n" +
                        "GetNetworkCountryIso() = " + mTelephonyManager.getNetworkCountryIso() + "\n" +
                        "GetNetworkOperator() = " + mTelephonyManager.getNetworkOperator() + "\n" +
                        "GetNetworkOperatorName() = " + mTelephonyManager.getNetworkOperatorName() + "\n" +
                        "GetNetworkType() = " + mTelephonyManager.getNetworkType() + getNetworkTypeText(
                        mTelephonyManager.getNetworkType()) + "\n" +
                        "GetPhoneType() = " + mTelephonyManager.getPhoneType() + "\n" +
                        "GetSimCountryIso() = " + mTelephonyManager.getSimCountryIso() + "\n" +
                        "GetSimOperator() = " + mTelephonyManager.getSimOperator() + "\n" +
                        "GetSimOperatorName() = " + mTelephonyManager.getSimOperatorName() + "\n" +
                        "GetSimSerialNumber() = " + mTelephonyManager.getSimSerialNumber() + "\n" +
                        "GetSimState() = " + mTelephonyManager.getSimState() + "\n" +
                        "GetSubscriberId() = " + mTelephonyManager.getSubscriberId() + "\n" +
                        "GetVoiceMailAlphaTag() = " + mTelephonyManager.getVoiceMailAlphaTag() + "\n" +
                        "GetVoiceMailNumber() = " + mTelephonyManager.getVoiceMailNumber() + "\n" +
                        "HasIccCard() = " + mTelephonyManager.hasIccCard() + "\n" +
                        "IsNetworkRoaming() = " + mTelephonyManager.isNetworkRoaming() + "\n");

        writeLog2SD(testMessage.toString(), false);

        return testMessage.toString();
    }

    private void writeLog2SD(String string) {
        writeLog2SD(string, true);
    }

    private void writeLog2SD(String string, boolean flag) {
        Log.i("TelephonyTest", string);
        if(flag) {
            string = DateUtil.getDateN() + string;
            mHandler.obtainMessage(0, string).sendToTarget();
        } else {
            string = "===========GetTelephonyParam===========\n" + string;
        }
        FileWriter out;
        try {
            out = new FileWriter(mFile, true);

            out.append("\n" + string + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getCallStateText(int state) {
        String text = "";

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                text = "CALL_STATE_IDLE";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                text = "CALL_STATE_OFFHOOK";
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                text = "CALL_STATE_RINGING";
                break;

        }

        return text;
    }

    protected String getDataActivityText(int direction) {
        String text = "";

        switch (direction) {
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                text = "DATA_ACTIVITY_DORMANT";
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                text = "DATA_ACTIVITY_IN";
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                text = "DATA_ACTIVITY_INOUT";
                break;
            case TelephonyManager.DATA_ACTIVITY_NONE:
                text = "DATA_ACTIVITY_NONE";
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                text = "DATA_ACTIVITY_OUT";
                break;
        }

        return text;

    }

    protected String getDataConnectionStateText(int state) {
        String text = "";

        switch (state) {
            case TelephonyManager.DATA_CONNECTED:
                text = "DATA_CONNECTED";
                break;
            case TelephonyManager.DATA_CONNECTING:
                text = "DATA_CONNECTING";
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                text = "DATA_DISCONNECTED";
                break;
            case TelephonyManager.DATA_SUSPENDED:
                text = "DATA_SUSPENDED";
                break;
        }

        return text;
    }

    protected String getNetworkTypeText(int networkType) {
        String networkTypeText = "";

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                networkTypeText = "NETWORK_TYPE_1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                networkTypeText = "NETWORK_TYPE_CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                networkTypeText = "NETWORK_TYPE_EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                networkTypeText = "NETWORK_TYPE_EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                networkTypeText = "NETWORK_TYPE_EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                networkTypeText = "NETWORK_TYPE_EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                networkTypeText = "NETWORK_TYPE_EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                networkTypeText = "NETWORK_TYPE_GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                networkTypeText = "NETWORK_TYPE_HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                networkTypeText = "NETWORK_TYPE_HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkTypeText = "NETWORK_TYPE_HSPAP";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                networkTypeText = "NETWORK_TYPE_HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkTypeText = "NETWORK_TYPE_IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkTypeText = "NETWORK_TYPE_LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                networkTypeText = "NETWORK_TYPE_UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                networkTypeText = "NETWORK_TYPE_UNKNOWN";
                break;
        }

        return networkTypeText;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
