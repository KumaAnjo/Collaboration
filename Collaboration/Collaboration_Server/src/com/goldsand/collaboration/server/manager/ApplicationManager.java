
package com.goldsand.collaboration.server.manager;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.goldsand.collaboration.commonprotocol.CommonModuleCombine;
import com.goldsand.collaboration.commonprotocol.CommonJson;
import com.goldsand.collaboration.commonprotocol.JsonParser;
import com.goldsand.collaboration.commonprotocol.JsonPacker;

import com.goldsand.collaboration.connection.Authorizer;
import com.goldsand.collaboration.connection.NetWorkInfo;
import com.goldsand.collaboration.connection.Utils;
import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;
import com.goldsand.collaboration.connection.Connection.ConnectionType;
import com.goldsand.collaboration.connection.ConnectionListener;

import com.goldsand.collaboration.server.authorize.AuthorizeCheckerCallBack;
import com.goldsand.collaboration.server.authorize.KnownDeviceSaver;
import com.goldsand.collaboration.server.connect.ConnectionImpl;

public class ApplicationManager {
    private static final String TAG = "ApplicationManager";
    private final boolean DEBUG = true;

    private ConnectionImpl mConnection;

    private static Object mLock = new Object();
    private HashMap<String, IMsgListener> mModuleMap = new HashMap<String, IMsgListener>();

    private static ApplicationManager mInstance;
    private KnownDeviceSaver mKnownDeviceSaver;

    private ApplicationManager(Context context) {
        mKnownDeviceSaver = new KnownDeviceSaver(context);
        mConnection = new ConnectionImpl(ConnectionType.TYPE_SERVER, mAuthorizer);
        mConnection.registConnectionListener(new ConnectionListener() {
            @Override
            public void onNewDataReceive(String data) {
                onDeliverData(data);
            }

            @Override
            public void onNewDataReceive(Object data) {
                if (DEBUG) {
                    Log.i(TAG, "onNewDataReceive()");
                }
            }

            @Override
            public void onConnectionStatusChanged(ConnectStatus newStatus) {
                if (DEBUG) {
                    Log.i(TAG, "onConnectionStatusChanged() newStatus = " + newStatus);
                }
            }
        });
        startConnect();
    }

    private AuthorizeCheckerCallBack mAuthorizeChecker;
    private Object mAuthorizeLock = new Object();
    private boolean mCheckResult = false;

    private void onCheckFinish(boolean checkResult) {
        synchronized (mAuthorizeLock) {
            mCheckResult = checkResult;
            mAuthorizeLock.notify();
        }
    }

    public void checkFail() {
        onCheckFinish(false);
    }

    public void checkPass(boolean saveDeviceInfo) {
        onCheckFinish(true);
        if (saveDeviceInfo) {
            mKnownDeviceSaver.saveKnownDevice(mDeviceName, mDeviceMac);
        }
    }

    public void setAuthorize(AuthorizeCheckerCallBack authorizeCheckerCallBack) {
        this.mAuthorizeChecker = authorizeCheckerCallBack;
    }

    private String mDeviceName;
    private String mDeviceMac;
    private Authorizer mAuthorizer = new Authorizer() {
        @Override
        public boolean authorize(String message) {
            mDeviceName = Utils.getDeviceName(message);
            mDeviceMac = Utils.getDeviceMac(message);
            if (mKnownDeviceSaver.isKnownDevice(mDeviceName, mDeviceMac)) {
                mCheckResult = false;
                return true;
            }
            synchronized (mAuthorizeLock) {
                mCheckResult = false;
                if (mAuthorizeChecker != null) {
                    mAuthorizeChecker.checkAuthorize(mDeviceName);
                    try {
                        mAuthorizeLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return mCheckResult;
            }
        }
    };

    public static ApplicationManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new ApplicationManager(context);
            }
        }

        return mInstance;
    }

    public void addListener(String moduleName, IMsgListener listener) {
        if (mModuleMap.get(moduleName) == null) {
            if (DEBUG) {
                Log.i(TAG, "addListener().moduleName = " + moduleName);
            }
            mModuleMap.put(moduleName, listener);
        }
    }

    public void removeListener(String module) {
        mModuleMap.remove(module);
    }

    public boolean startConnect() {
        if (DEBUG) {
            Log.i(TAG, "startConnect().mConnection is null ? " + (mConnection == null));
        }
        if (mConnection != null) {
            mConnection.start();
            return false;
        }
        return false;
    }

    public boolean stopConnect() {
        if (mConnection != null) {
            mConnection.stop();
            return true;
        }
        return false;
    }

    public NetWorkInfo getNetworkInfo() {
        if (mConnection != null) {
            return mConnection.getNetWorkInfo();
        }
        return null;
    }

    public boolean sendData(String module, JSONObject upLevelJsonObj) {
        CommonJson commObj = new CommonJson();
        commObj.setModule(module);

        JsonPacker packer = new JsonPacker();
        String data = packer.packToJson(commObj, upLevelJsonObj);
        if (DEBUG) {
            Log.i(TAG, "sendData().data = " + data);
        }

        if (mConnection != null) {
            mConnection.sendDataToTarget(data);
        }

        return false;
    }

    public void onDeliverData(String data) {
        if (DEBUG) {
            Log.i(TAG, "onDeliverData().data = " + data);
        }
        CommonModuleCombine combineObj = null;
        try {
            JsonParser parser = new JsonParser();
            combineObj = parser.parse(data);
            String module = combineObj.getCommObj().getModule();
            IMsgListener tListener = mModuleMap.get(module);
            if (DEBUG) {
                Log.i(TAG, "onDeliverData().tListener is null ? " + (tListener == null));
            }
            if (tListener != null) {
                tListener.onMessage(combineObj.getModuleJSONObj());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
