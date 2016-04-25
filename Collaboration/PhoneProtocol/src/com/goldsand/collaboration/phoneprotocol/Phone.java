package com.goldsand.collaboration.phoneprotocol;

public class Phone {
    public enum State {
        IDLE,INCOMING, OFFHOOK;
    }

    public static String getState(State state) {
        String stateString = "Unknow";
        if (state == State.IDLE) {
            stateString = "Idle";
        } else if (state == State.INCOMING) {
            stateString = "Incoming";
        } else if (state == State.OFFHOOK) {
            stateString = "Offhook";
        }

        return stateString;
    }
}
