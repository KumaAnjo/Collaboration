
package com.goldsand.collaboration.server;

import java.lang.reflect.Method;
import com.android.internal.telephony.ITelephony;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneUtils {
    private static final String TAG = "PhoneUtils";

    public static void answerCall(Context context) {
        final TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService("phone");
        if (mTelephonyManager != null) {
            try {
                Method method = Class.forName(mTelephonyManager.getClass().getName()).getDeclaredMethod("getITelephony");
                method.setAccessible(true);
                Object v3 = method.invoke(mTelephonyManager);
//                ((ITelephony) v3).silenceRinger();
                Log.v(TAG, "Answering Call now...");
                ((ITelephony) v3).answerRingingCall();
                Log.v(TAG, "Call answered...");
            } catch (Exception ex) {
                Log.w(TAG, "FATAL ERROR: could not connect to telephony subsystem");
                Log.w(TAG, "Exception object: " + ex);
            }
        }
    }

    public static void endCall(Context context) {
        final TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService("phone");
        if (mTelephonyManager != null) {
            try {
                Method method = Class.forName(mTelephonyManager.getClass().getName()).getDeclaredMethod("getITelephony");
                method.setAccessible(true);
                Object v3 = method.invoke(mTelephonyManager);
                Log.v(TAG, "End Call now...");
                ((ITelephony) v3).endCall();
            } catch (Exception ex) {
                Log.w(TAG, "FATAL ERROR: could not connect to telephony subsystem");
                Log.w(TAG, "Exception object: " + ex);
            }
        }
    }

}
