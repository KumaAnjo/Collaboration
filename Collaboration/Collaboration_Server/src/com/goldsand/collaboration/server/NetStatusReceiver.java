package com.goldsand.collaboration.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean wifiConnected = wifiInfo.isConnected();
            Log.d(TAG, "on receive boradcast action = " + action + " wifi connected:"
                    + wifiConnected);

            if (wifiConnected) {
                Intent collaborationIntent = new Intent(context, CollaborationService.class);
                context.startService(collaborationIntent);
            }
        }
    }
}
