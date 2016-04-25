package com.goldsand.collaboration.connection;

import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatThread{
    public static final int HEARTHEAT_FREQUENCY = 10 * 1000;
    public static final int HEARTHEAT_TIME_OUT = 15 * 1000;

    private SocketDataSenderThread mDataSenderThread;
    private Timer mTimer;

    public HeartbeatThread(SocketDataSenderThread dataSenderThread) {
        mDataSenderThread = dataSenderThread;
        mTimer = new Timer();
    }

    public void start() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mDataSenderThread.addHeartbeatPackage();
            }
        }, HEARTHEAT_FREQUENCY, HEARTHEAT_FREQUENCY);
    }

    public void stop() {
        mTimer.cancel();
    }

}
