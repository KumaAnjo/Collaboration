package com.goldsand.collaboration.server;

import org.json.JSONObject;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.goldsand.collaboration.connection.Authorizer;
import com.goldsand.collaboration.phoneprotocol.Call;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJson;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonParser;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonPacker;

import com.goldsand.collaboration.server.authorize.AuthorizeCheckerActivity;
import com.goldsand.collaboration.server.authorize.AuthorizeCheckerCallBack;
import com.goldsand.collaboration.server.manager.ApplicationManager;
import com.goldsand.collaboration.server.manager.IMsgListener;
import com.goldsand.collaboration.server.phone.audio.AudioManager;

public class CollaborationService extends Service implements IMsgListener{
    private static final String TAG = "CollaborationService";
    private TelephonyManager mTm;
    private MyPhoneStateListener mPhoneStateListener;
    private AudioManager mAudioManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                sendDataToClient(packPhoneJson(state, incomingNumber));
                // TEMP
                stopAudio();
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                sendDataToClient(packPhoneJson(state, incomingNumber));
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                sendDataToClient(packPhoneJson(state, incomingNumber));
                break;
            default:
                break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private AuthorizeCheckerCallBack mAuthorizeCallBack = new AuthorizeCheckerCallBack() {
        @Override
        public void checkAuthorize(String key) {
            Intent intent = new Intent(getApplicationContext(), AuthorizeCheckerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", key);
            startActivity(intent);
        }
    };

    public JSONObject packPhoneJson(int callState, String incomingNumber) {
        String strJson;
        PhoneJson phoneJson = new PhoneJson();
        phoneJson.setCallType(stateFromPhoneToCall(callState));
        phoneJson.setNumber(incomingNumber);
        // Todo: Here we use incomingNumber as Serialno, in the future we will use a serial number
        phoneJson.setSerialno(incomingNumber);

        PhoneJsonPacker packer = new PhoneJsonPacker();
        return packer.getPhoneJsonObject(phoneJson);
    }

    private int stateFromPhoneToCall(int phoneState) {
        Call.State state;
        if (TelephonyManager.CALL_STATE_IDLE == phoneState) {
            state = Call.State.IDLE;
        } else if (TelephonyManager.CALL_STATE_RINGING == phoneState) {
            state = Call.State.INCOMING;
        } else if (TelephonyManager.CALL_STATE_OFFHOOK == phoneState) {
            state = Call.State.ACTIVE;
        } else {
            state = Call.State.IDLE;
        }

        return Call.stateToNum(state);
    }

    public void sendDataToClient(JSONObject jsonObj) {
        ApplicationManager.getInstance(this).sendData(com.goldsand.collaboration.phoneprotocol.Module.PHONE, jsonObj);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPhoneStateListener = new MyPhoneStateListener();
        getTelephonyManager().listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        mAudioManager = new AudioManager(this);

        // add listener
        ApplicationManager.getInstance(this).addListener(
                com.goldsand.collaboration.phoneprotocol.Module.PHONE, this);
        ApplicationManager.getInstance(this).setAuthorize(mAuthorizeCallBack);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void unregisterPhoneState() {
        getTelephonyManager().listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private TelephonyManager getTelephonyManager() {
        if (mTm == null) {
            mTm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        }
        return mTm;
    }

    @Override
    public void onMessage(JSONObject dataObj) {
        Log.i(TAG, "onMessage().dataObj = " + dataObj);

        PhoneJsonParser parser = new PhoneJsonParser();
        PhoneJson phoneJsonObj = parser.parser(dataObj);

        if (phoneJsonObj != null) {
            Log.i(TAG, "onMessage().call type = " + phoneJsonObj.getCallType());
            Log.i(TAG, "onMessage().call serialNo = " + phoneJsonObj.getSerialno());
            if (Call.State.ACTIVE == Call.numToState(phoneJsonObj.getCallType())) {
                // create audio connect
                if (mAudioManager != null) {
                    String remoteIP = ApplicationManager.getInstance(this).getNetworkInfo().getRemoteIp();
                    mAudioManager.setRemoteIP(remoteIP);
                    mAudioManager.prepareAudio();
                    mAudioManager.startAudio();
                }
                // Answer call
                PhoneUtils.answerCall(this);
            } else if (Call.State.DISCONNECTING == Call.numToState(phoneJsonObj.getCallType())) {
                // Reject call
                PhoneUtils.endCall(this);

                stopAudio();
            }
        }
    }

    private void stopAudio() {
        // audio disconnect
        if (mAudioManager != null) {
            mAudioManager.stopAudio();
        }
    }
}
