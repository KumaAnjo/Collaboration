package com.goldsand.collaboration.client.phone;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.goldsand.collaboration.phoneprotocol.base.PhoneJson;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonParser;

import com.goldsand.collaboration.client.manager.IMsgListener;

public class PhoneMsgListener implements IMsgListener {
    private static final String TAG = "PhoneMsgListener";

    private Context mContext;
    private Handler mHandler;

    private ArrayList<PhoneJson> jsonList = new ArrayList<PhoneJson>();

    public PhoneMsgListener(Context context) {
        mContext = context;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;

        deliverMsg();
    }

    private void deliverMsg() {
        while (!jsonList.isEmpty()) {
            updateState(InCallUIActivity.MSG_DEVOLVE_EVENT, jsonList.remove(0));
        }
    }

    public void onMessage(JSONObject dataObj) {
        PhoneJsonParser parser = new PhoneJsonParser();
        PhoneJson phoneJsonObj = parser.parser(dataObj);
        jsonList.add(phoneJsonObj);

        Log.i(TAG, "onMessage().mHandler is null ? " + (mHandler == null));
        if (mHandler != null) {
            deliverMsg();
        } else {
            //Intent intent = new Intent(mContext, InCallUIActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent intent = new Intent(mContext, InCallUIService.class);
            try {
                //mContext.startActivity(intent);
                mContext.startService(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateState(int messageid, PhoneJson jsonObj) {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage(messageid);
            message.obj = jsonObj;
            message.sendToTarget();
        } else {
            Log.i(TAG, "updateState().mHandler is null");
        }
    }
}
