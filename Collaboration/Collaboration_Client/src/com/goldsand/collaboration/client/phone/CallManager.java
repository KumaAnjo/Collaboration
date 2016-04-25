package com.goldsand.collaboration.client.phone;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.android.collect.Maps;

import com.goldsand.collaboration.phoneprotocol.base.PhoneJson;

public class CallManager {
    private final HashMap<String, PhoneJson> mCallMap = Maps.newHashMap();

    public CallManager() {
        //
    }

    public PhoneJson getCallWithId(String serialno) {
        for (Entry<String, PhoneJson> entry : mCallMap.entrySet()) {
            if (entry.getValue().getSerialno() == serialno) {
                return entry.getValue();
            }
        }
        return null;
    }

    public PhoneJson updateCall(PhoneJson json) {
        PhoneJson internalCall = getCallWithId(json.getSerialno());
        if (internalCall != null) {
            if (internalCall != null) {
                return internalCall.copy(json);
            }
        } else {
            return addCall(json.getSerialno(), json);
        }
        return null;
    }

    public PhoneJson addCall(String serialno, PhoneJson json) {
        return mCallMap.put(serialno, json);
    }

    public PhoneJson removeCall(String serialno) {
        return mCallMap.remove(serialno);
    }

    public void removeAll() {
        for (Entry<String, PhoneJson> entry : mCallMap.entrySet()) {
            mCallMap.remove(entry.getValue().getSerialno());
        }
    }

    public int getCallsCount(){
        return mCallMap.keySet().size();
    }
}
