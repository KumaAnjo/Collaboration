package com.goldsand.collaboration.client.phone;

import com.goldsand.collaboration.phoneprotocol.Call;

import com.goldsand.collaboration.client.R;

public class Utils {
    public static int stateToString(Call.State state) {
        int res = -1;
        if (state == Call.State.IDLE) {
            res = R.string.call_state_idle;
        } else if (state == Call.State.INCOMING) {
            res = R.string.call_state_incoming;
        } else if (state == Call.State.ACTIVE) {
            res = R.string.call_state_active;
        } else if (state == Call.State.HOLDING){
            res = R.string.call_state_holding;
        }
        return res;
    }
}
