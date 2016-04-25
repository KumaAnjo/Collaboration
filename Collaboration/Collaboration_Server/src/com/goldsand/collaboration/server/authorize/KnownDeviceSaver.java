package com.goldsand.collaboration.server.authorize;

import android.content.Context;
import android.content.SharedPreferences;

import com.goldsand.collaboration.connection.Utils;

public class KnownDeviceSaver {
    private Context mContext;
    private static final String KNOWN_DEVICE_SAVE_PREFERENCE = "known_device_save_preference";
    private SharedPreferences mSharedPreferences;

    public KnownDeviceSaver(Context context) {
        this.mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(KNOWN_DEVICE_SAVE_PREFERENCE, Context.MODE_PRIVATE);
    }

    public boolean isKnownDevice(String deviceName,String mac) {
        String saveMac = mSharedPreferences.getString(deviceName, null);
        if (Utils.isEmpty(saveMac)) {
            return false;
        }
        String encryptMac = Utils.createEncrypyMac(mac);
        return saveMac.equals(encryptMac);
    }

    public void saveKnownDevice(String deviceName, String mac) {
        String encryptMac = Utils.createEncrypyMac(mac);
        mSharedPreferences.edit().putString(deviceName, encryptMac).apply();
    }

}
