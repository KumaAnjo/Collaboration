package com.goldsand.collaboration.client.phone;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldsand.collaboration.client.ControlCenterDeamonService;
import com.goldsand.collaboration.client.R;
import com.goldsand.collaboration.client.manager.ApplicationManager;
import com.goldsand.collaboration.client.manager.IMsgListener;
import com.goldsand.collaboration.client.phone.audio.AudioManager;

import com.goldsand.collaboration.phoneprotocol.Call;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJson;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonParser;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonPacker;

public class InCallUIActivity extends Activity implements ServiceConnection, OnClickListener, OnTouchListener {

    private static final String TAG = "ClientActivity";
    public static final int MSG_DEVOLVE_EVENT = 1;
    private static final int MSG_IDLE_CALL = 2;
    private static final int MSG_INCOMING_CALL = 3;
    private static final int MSG_ACTIVE_CALL = 4;
    private static final int MSG_END_CALL = 5;

    private ControlCenterDeamonService mControlService;
    private CallManager mCallManager;
    private AudioManager mAudioManager;

    private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;

    private TextView mCallInfo_tv;
    private TextView mTimerView;
    private TextView mCallStatusView;
    private Button mAcceptBtn;
    private Button mRejectBtn;
    private Button mEndCallBtn;
    private Button mIgnoreBtn;
    private LinearLayout mAnswerCallLayout;
    private boolean mIsAnsweredByme;

    protected int xlocation;
    protected int ylocation;
    private Call.State mCallState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCallManager = new CallManager();
        mAudioManager = new AudioManager(this);

        //createFloatView();

        // bind service
        bindService();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent()");
    }

    private void handleDevolveMsg(PhoneJson phoneJsonObj) {
        Log.i(TAG, "onMessage().phoneJsonObj is null ? " + (phoneJsonObj == null));

        if (phoneJsonObj == null)
            return;
        String message = phoneJsonObj.toString();
        Log.i(TAG, "onNewMessageReceived(). call type = " + phoneJsonObj.getCallType() + "; message = " + message);
        Call.State state = Call.numToState(phoneJsonObj.getCallType());
        if (state == Call.State.IDLE) {
            updateState(MSG_IDLE_CALL, phoneJsonObj);
            mCallState = Call.State.IDLE;
        } else if (state == Call.State.INCOMING) {
            updateState(MSG_INCOMING_CALL, phoneJsonObj);
            mCallState = Call.State.INCOMING;
        } else if (state == Call.State.ACTIVE) {
            updateState(MSG_ACTIVE_CALL, phoneJsonObj);
            mCallState = Call.State.ACTIVE;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Log.i(TAG, "handleMessage(). what = " + msg.what);
            switch (msg.what) {
            case MSG_DEVOLVE_EVENT:
                PhoneJson jsonObj = (PhoneJson) msg.obj;
                handleDevolveMsg(jsonObj);
                break;
            case MSG_IDLE_CALL:
                dismissFloatUI();
                break;
            case MSG_INCOMING_CALL:
                PhoneJson phoneJson = (PhoneJson) msg.obj;
                if (mFloatLayout == null) {
                    createFloatView();
                }
                mCallInfo_tv.setText(phoneJson.getNumber());
                mCallStatusView.setText(getResources().getString(Utils.stateToString(mCallState)));
                break;
            case MSG_ACTIVE_CALL:
                if (mIsAnsweredByme) {
                    mAnswerCallLayout.setVisibility(View.GONE);
                    mEndCallBtn.setVisibility(View.VISIBLE);
                    mTimerView.setText("0:06");
                    mCallStatusView.setText(getResources().getString(Utils.stateToString(mCallState)));
                } else {
                    dismissFloatUI();
                }
                break;
            default:
                break;
            }
        };
    };

    private void updateState(int messageid, PhoneJson jsonObj) {
        Message message = mHandler.obtainMessage(messageid);
        message.obj = jsonObj;
        message.sendToTarget();
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(
                getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 5;
        wmParams.y = 10;

        wmParams.width = 500 /* WindowManager.LayoutParams.WRAP_CONTENT */;
        wmParams.height = 250/* WindowManager.LayoutParams.WRAP_CONTENT */;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.incall_ui,
                null);
        mWindowManager.addView(mFloatLayout, wmParams);
        mAcceptBtn = (Button) mFloatLayout.findViewById(R.id.btn_accept);
        mRejectBtn = (Button) mFloatLayout.findViewById(R.id.btn_reject);
        mIgnoreBtn = (Button) mFloatLayout.findViewById(R.id.btn_ignore);
        mEndCallBtn = (Button) mFloatLayout.findViewById(R.id.btn_endcall);
        mCallInfo_tv = (TextView) mFloatLayout.findViewById(R.id.number);
        mTimerView = (TextView) mFloatLayout.findViewById(R.id.timer);
        mCallStatusView = (TextView) mFloatLayout.findViewById(R.id.call_status);

        mAnswerCallLayout = (LinearLayout) mFloatLayout
                .findViewById(R.id.answer_layout);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mAcceptBtn.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mAcceptBtn.getMeasuredHeight() / 2);

        mAcceptBtn.setOnClickListener(this);
        mRejectBtn.setOnClickListener(this);
        mEndCallBtn.setOnClickListener(this);
        mIgnoreBtn.setOnClickListener(this);
        mFloatLayout.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_accept:
            acceptCall();
            break;
        case R.id.btn_endcall:
        case R.id.btn_reject:
            rejectCall();
            dismissFloatUI();
            break;
        case R.id.btn_ignore:
            dismissFloatUI();
            break;
        default:
            break;
        }
    }

    private void acceptCall() {
        mIsAnsweredByme = true;
        ApplicationManager.getInstance(this).sendData(com.goldsand.collaboration.phoneprotocol.Module.PHONE, packPhoneJson(Call.State.ACTIVE, ""));

        // create audio connection
        if (mAudioManager != null) {
            String remoteIP = "";
            if (mControlService != null) {
                remoteIP = mControlService.getApplicationManager().getNetworkInfo().getRemoteIp();
            }
            mAudioManager.setRemoteIP(remoteIP);
            mAudioManager.prepareAudio();
            mAudioManager.startAudio();
        }
    }

    private void rejectCall() {
        ApplicationManager.getInstance(this).sendData(com.goldsand.collaboration.phoneprotocol.Module.PHONE, packPhoneJson(Call.State.DISCONNECTING, ""));

        // audio disconnect
        if (mAudioManager != null) {
            mAudioManager.stopAudio();
        }
    }

    private void dismissFloatUI() {
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
            mFloatLayout = null;
        }

        if (mControlService != null) {
            mControlService.getPhoneListener().setHandler(null);
            unbindService(this);
        }

        mIsAnsweredByme = false;
    }

    public JSONObject packPhoneJson(Call.State callState, String incomingNumber) {
        String strJson;
        PhoneJson phoneJson = new PhoneJson();
        phoneJson.setCallType(Call.stateToNum(callState));
        phoneJson.setNumber(incomingNumber);

        PhoneJsonPacker packer = new PhoneJsonPacker();
        return packer.getPhoneJsonObject(phoneJson);
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            // TODO Auto-generated method stub
            xlocation = (int) event.getRawX();
            ylocation = (int) event.getRawY();
            wmParams.x = xlocation - mFloatLayout.getWidth() / 2;
            wmParams.y = ylocation - mFloatLayout.getHeight() / 2 - 25;
            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            break;
        case MotionEvent.ACTION_UP:
            break;
        default:
            break;
        }

        return false;
    }

    private void bindService() {
        Intent intent = new Intent(this, ControlCenterDeamonService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName arg0, IBinder arg1) {
        Log.i(TAG, "onServiceConnected().arg1 is null ? " + (arg1 == null));

        mControlService = ((ControlCenterDeamonService.TSBinder) arg1).getService();
        mControlService.getPhoneListener().setHandler(mHandler);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        Log.i(TAG, "onServiceDisconnected().arg0 is null ? " + (arg0 == null));
    }

}
