package com.goldsand.collaboration.phoneprotocol;

public class Call {
    public enum State {
        IDLE, ACTIVE, HOLDING, DIALING, ALERTING, INCOMING, WAITING, DISCONNECTED, DISCONNECTING;
    }

    public static int stateToNum(State state) {
        int stateInt = -1;
        if (State.IDLE == state) {
            stateInt = 0;
        } else if (State.ACTIVE == state) {
            stateInt = 1;
        } else if (State.HOLDING == state) {
            stateInt = 2;
        } else if (State.DIALING == state) {
            stateInt = 3;
        } else if (State.ALERTING == state) {
            stateInt = 4;
        } else if (State.INCOMING == state) {
            stateInt = 5;
        } else if (State.WAITING == state) {
            stateInt = 6;
        } else if (State.DISCONNECTED == state) {
            stateInt = 7;
        } else if (State.DISCONNECTING == state) {
            stateInt = 8;
        }
        return stateInt;
    }

    public static State numToState(int num) {
        State state;
        switch (num) {
            case 0:
                state = State.IDLE;
                break;
            case 1:
                state = State.ACTIVE;
                break;
            case 2:
                state = State.HOLDING;
                break;
            case 3:
                state = State.DIALING;
                break;
            case 4:
                state = State.ALERTING;
                break;
            case 5:
                state = State.INCOMING;
                break;
            case 6:
                state = State.WAITING;
                break;
            case 7:
                state = State.DISCONNECTED;
                break;
            case 8:
                state = State.DISCONNECTING;
                break;
            default:
                state = State.IDLE;
        }
        return state;
    }
}
