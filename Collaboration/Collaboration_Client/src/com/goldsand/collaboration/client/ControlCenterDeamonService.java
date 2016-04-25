package com.goldsand.collaboration.client;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.goldsand.collaboration.client.manager.ApplicationManager;
import com.goldsand.collaboration.client.phone.InCallUIActivity;
import com.goldsand.collaboration.client.phone.PhoneMsgListener;

public class ControlCenterDeamonService extends Service {
    private static final String TAG = "ControlCenterDeamonService";

    private PhoneMsgListener mPhoneListener;

    @Override
    public void onCreate(){
        super.onCreate();

        mPhoneListener = new PhoneMsgListener(this);

        registerCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return new TSBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ApplicationManager getApplicationManager() {
        return ApplicationManager.getInstance(this);
    }

    public PhoneMsgListener getPhoneListener() {
        return mPhoneListener;
    }

    private void registerCallback() {
        // Phone
        ApplicationManager.getInstance(this).addListener(com.goldsand.collaboration.phoneprotocol.Module.PHONE, mPhoneListener);
        // Message
        // Browser
    }

    public class TSBinder extends Binder {
        public ControlCenterDeamonService getService() {
            return ControlCenterDeamonService.this;
        }
    }
}
