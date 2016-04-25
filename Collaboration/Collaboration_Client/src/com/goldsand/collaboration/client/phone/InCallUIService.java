package com.goldsand.collaboration.client.phone;

import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.app.Service;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldsand.collaboration.client.ControlCenterDeamonService;
import com.goldsand.collaboration.client.R;
import com.goldsand.collaboration.client.manager.ApplicationManager;
import com.goldsand.collaboration.client.manager.IMsgListener;
import com.goldsand.collaboration.client.phone.audio.AudioManager;
import com.goldsand.collaboration.phoneprotocol.Call;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJson;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonParser;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonPacker;

public class InCallUIService extends Service implements ServiceConnection, OnTouchListener {
    private static final String TAG = "InCallUIService";

    public static final int MSG_DEVOLVE_EVENT = 1;
    private static final int MSG_IDLE_CALL = 2;
    private static final int MSG_INCOMING_CALL = 3;
    private static final int MSG_ACTIVE_CALL = 4;
    private static final int MSG_END_CALL = 5;

    private ControlCenterDeamonService mControlService;
    private CallManager mCallManager;
    private AudioManager mAudioManager;

    private FloatViewHolder mFloatViewHolder;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;

    private boolean mIsAnsweredByme;

    protected int xlocation;
    protected int ylocation;
    private Call.State mCallState;
    private HashMap<String,FloatViewHolder> mFloatViewMap= new HashMap<String,FloatViewHolder>();

    @Override
    public void onCreate() {
        super.onCreate();

        mCallManager = new CallManager();
        mAudioManager = new AudioManager(this);

        // bind service
        bindService();
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
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
                PhoneJson phoneJsonidle = (PhoneJson) msg.obj;
                String serialNum = phoneJsonidle.getSerialno();

                // dismiss float UI
                if(mFloatViewMap.get(serialNum) != null){
                    dismissFloatUI(mFloatViewMap.get(serialNum).mParentView, serialNum);
                }

                // remove the call from callManager
                mCallManager.removeCall(phoneJsonidle.getSerialno());

                // clear callManager and stop audio if no call in callManager
                clearInCallUI();
                break;

            case MSG_INCOMING_CALL:
                PhoneJson phoneJson = (PhoneJson) msg.obj;
                String serialNo =  phoneJson.getSerialno();

                mFloatViewHolder= mFloatViewMap.get(serialNo);
                if (mFloatViewHolder == null) {
                    if(mCallManager.getCallsCount() == 0){
                        mFloatViewHolder = createFloatView(5, 10, serialNo);
                    }else{
                        // more than 1 call, create a new float view
                        mFloatViewHolder = createFloatView(5, 300, serialNo);
                    }
                    mFloatViewMap.put(serialNo, mFloatViewHolder);
                }

                mFloatViewHolder.mCallInfo_tv.setText(phoneJson.getNumber());
                mFloatViewHolder.mCallStatusView.setText(getResources().getString(Utils.stateToString(mCallState)));
                mCallManager.addCall(serialNo, phoneJson);
                break;
            case MSG_ACTIVE_CALL:
                // We can not get the serial number and phone number here
                if (mIsAnsweredByme) {
                    mFloatViewHolder.mAnswerCallLayout.setVisibility(View.GONE);
                    mFloatViewHolder.mEndCallBtn.setVisibility(View.VISIBLE);
                    mFloatViewHolder.mTimerView.setText("0:06");
                    mFloatViewHolder.mCallStatusView.setText(getResources().getString(Utils.stateToString(mCallState)));
                } else {
                    // ToDo:we should dismiss the floatUI, but we can not get serial number
                    //to dismiss which one.
                    //dismissFloatUI();
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

    private FloatViewHolder createFloatView(int x, int y, String serialNo) {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(
                getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = x;
        wmParams.y = y;

        wmParams.width = 500 /* WindowManager.LayoutParams.WRAP_CONTENT */;
        wmParams.height = 250/* WindowManager.LayoutParams.WRAP_CONTENT */;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        LinearLayout floatLayout = (LinearLayout) inflater.inflate(R.layout.incall_ui,
                null);
        mWindowManager.addView(floatLayout, wmParams);
        FloatViewHolder floatViewHolder =new FloatViewHolder();
        floatViewHolder.mAcceptBtn = (Button) floatLayout.findViewById(R.id.btn_accept);
        floatViewHolder.mRejectBtn = (Button) floatLayout.findViewById(R.id.btn_reject);
        floatViewHolder.mIgnoreBtn = (Button) floatLayout.findViewById(R.id.btn_ignore);
        floatViewHolder.mEndCallBtn = (Button) floatLayout.findViewById(R.id.btn_endcall);
        floatViewHolder.mCallInfo_tv = (TextView) floatLayout.findViewById(R.id.number);
        floatViewHolder.mTimerView = (TextView) floatLayout.findViewById(R.id.timer);
        floatViewHolder.mCallStatusView = (TextView) floatLayout.findViewById(R.id.call_status);
        floatViewHolder.mParentView = floatLayout;

        floatViewHolder.mAnswerCallLayout = (LinearLayout) floatLayout
                .findViewById(R.id.answer_layout);

        floatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + floatViewHolder.mAcceptBtn.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + floatViewHolder.mAcceptBtn.getMeasuredHeight() / 2);
        MyOnClickListener mylistner = new MyOnClickListener(floatLayout,serialNo);
        floatViewHolder.mAcceptBtn.setOnClickListener(mylistner);
        floatViewHolder.mRejectBtn.setOnClickListener(mylistner);
        floatViewHolder.mEndCallBtn.setOnClickListener(mylistner);
        floatViewHolder.mIgnoreBtn.setOnClickListener(mylistner);
        floatLayout.setOnTouchListener(this);
        return floatViewHolder;
    }

    private void acceptCall(String serialNo) {
        mIsAnsweredByme = true;
        ApplicationManager.getInstance(this).sendData(com.goldsand.collaboration.phoneprotocol.Module.PHONE, packPhoneJson(Call.State.ACTIVE, serialNo));

        // create audio connection
        startAudio();
    }

    private void rejectCall(String serialNo) {
        ApplicationManager.getInstance(this).sendData(com.goldsand.collaboration.phoneprotocol.Module.PHONE, packPhoneJson(Call.State.DISCONNECTING, serialNo));
    }

    private void mute() {
        //
    }

    private void ignore() {
        //
    }

    private void holdOn() {
        //
    }

    private void startAudio() {
      if (mAudioManager != null) {
            String remoteIP = "";
            if (mControlService != null) {
                remoteIP = mControlService.getApplicationManager().getNetworkInfo().getRemoteIp();
                Log.i(TAG, mControlService.getApplicationManager().getNetworkInfo().toString());
            }
            mAudioManager.setRemoteIP(remoteIP);
            mAudioManager.prepareAudio();
            mAudioManager.startAudio();
        }
    }

    private void stopAudio() {
        if (mAudioManager != null) {
            mAudioManager.stopAudio();
        }
    }

    private void clearInCallUI() {
        // if no call in callManager, clear call list and stop Audio
        if (mCallManager.getCallsCount() == 0) {
            // clear Call list
            if (mCallManager != null) {
                mCallManager.removeAll();
            }

            // audio disconnect
            stopAudio();

            // stop service
            stopSelf();
        }
    }

    private void dismissFloatUI(ViewGroup rootView,String ss) {
        mFloatViewMap.remove(ss);

        if (rootView != null) {
            mWindowManager.removeView(rootView);
            rootView = null;
        }

        if (mControlService != null && mCallManager.getCallsCount() == 0) {
            mControlService.getPhoneListener().setHandler(null);
            unbindService(this);
        }

        mIsAnsweredByme = false;
    }

    public JSONObject packPhoneJson(Call.State callState, String serialNo) {
        String strJson;
        PhoneJson phoneJson = new PhoneJson();
        phoneJson.setCallType(Call.stateToNum(callState));
        phoneJson.setSerialno(serialNo);
        PhoneJsonPacker packer = new PhoneJsonPacker();
        return packer.getPhoneJsonObject(phoneJson);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            xlocation = (int) event.getRawX();
            ylocation = (int) event.getRawY();
            wmParams.x = xlocation - view.getWidth() / 2;
            wmParams.y = ylocation - view.getHeight() / 2 - 25;
            mWindowManager.updateViewLayout(view, wmParams);
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

    class MyOnClickListener implements View.OnClickListener{
        private ViewGroup mParentView;
        private String mSerialNo;
        public MyOnClickListener(ViewGroup parentView,String serialNo){
            this.mParentView= parentView;
            this.mSerialNo = serialNo;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_accept:
                Log.d(TAG, "onClick() Accept serialNo =" + mSerialNo);
                acceptCall(mSerialNo);
                // Todo fake other floatview call state to HOLDING
                updateOtherFloat(mSerialNo, Call.State.HOLDING);
                break;
            case R.id.btn_endcall:
            case R.id.btn_reject:
                Log.d(TAG, "onClick() Reject serialNo =" + mSerialNo);
                rejectCall(mSerialNo);
                dismissFloatUI(mParentView, mSerialNo);
                // Todo fake other floatview call state to ACTIVE
                updateOtherFloat(mSerialNo, Call.State.ACTIVE);
                break;
            case R.id.btn_ignore:
                Log.d(TAG, "onClick() ignore serialNo =" + mSerialNo);
                dismissFloatUI(mParentView, mSerialNo);
                break;
            default:
                break;
            }
        }
    }

    private void updateOtherFloat(String serialNo, Call.State state){
        for (Entry<String, FloatViewHolder> entry : mFloatViewMap.entrySet()) {
            if (entry.getKey() != serialNo) {
                FloatViewHolder holder  =  entry.getValue();
                holder.mCallStatusView.setText(getResources().getString(Utils.stateToString(state)));
            }
        }
    }

    class FloatViewHolder{
       private ViewGroup mParentView;
       private TextView mCallInfo_tv;
       private TextView mTimerView;
       private TextView mCallStatusView;
       private Button mAcceptBtn;
       private Button mRejectBtn;
       private Button mEndCallBtn;
       private Button mIgnoreBtn;
       private LinearLayout mAnswerCallLayout;
    }
}
