package com.goldsand.collaboration.client.connect;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;

import com.goldsand.collaboration.connection.Connection;

public class ConnectionImpl extends Connection {
    private Context mContext;

    public ConnectionImpl(ConnectionType type,Context context) {
        super(type);
        mContext = context;
    }

    public ConnectionImpl(ConnectionType type) {
        super(type);
    }

    @Override
    public int getServerBroadcastPort() {
        return 10081;
    }

    @Override
    public int getClientBroadcastPort() {
        return 10082;
    }

    @Override
    public int getConnectionPort() {
        return 10083;
    }

    @Override
    public String getLocalHostIp() {
        // Do not invoked by client.
        return null;
    }

    @Override
    protected String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
           return capitalize(model);
        } else {
           return capitalize(manufacturer) + " " + model;
        }
    }

    @Override
    protected String getDeviceMac() {
        String deviceId = Secure.getString(mContext.getContentResolver(),
                Secure.ANDROID_ID);
        return deviceId;
    }

     private String capitalize(String s) {
         if (s == null || s.length() == 0) {
             return "";
         }
         char first = s.charAt(0);
         if (Character.isUpperCase(first)) {
             return s;
         } else {
             return Character.toUpperCase(first) + s.substring(1);
         }
     }
}
