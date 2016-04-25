
package com.goldsand.collaboration.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

class SocketDataSenderThread extends Thread {
    public static class SocketDataSendTask {
        private int id;
        private Object dataToSend;
        private MessageSendListener listener;

        public SocketDataSendTask(Object dataToSend, MessageSendListener listener) {
            this.dataToSend = dataToSend;
            this.listener = listener;
        }
    }

    private static final String TAG = "SocketDataSenderThread";
    private List<SocketDataSendTask> mMessageTask = new ArrayList<SocketDataSendTask>();
    private boolean mThreadStop = false;
    private ObjectOutputStream mOutputStream = null;
    private int mCurrentId = 0;
    private HeartbeatThread mHeartbeatThread;
    private boolean mHeartbeatEnabled = false;

    /**
     * 
     * @param outputStream The data from.
     * @param enableHearbeat true enable heartbeat else disable
     */
    public SocketDataSenderThread(String name, ObjectOutputStream outputStream, boolean enableHearbeat) {
        super(name+ ":sender");
        this.mOutputStream = outputStream;
        this.mHeartbeatEnabled = enableHearbeat;
    }

    public SocketDataSenderThread(ObjectOutputStream outputStream, boolean enableHearbeat) {
        this.mOutputStream = outputStream;
        this.mHeartbeatEnabled = enableHearbeat;
    }

    public int addMessage(SocketDataSendTask message) {
        synchronized (mMessageTask) {
            mCurrentId++;
            message.id = mCurrentId;
            mMessageTask.add(message);
            mMessageTask.notify();
            return mCurrentId;
        }
    }

    void addHeartbeatPackage() {
        synchronized (mMessageTask) {
            InnerPackage heartbeatPackage = new InnerPackage();
            SocketDataSendTask dataSendTask = new SocketDataSendTask(heartbeatPackage, null);
            dataSendTask.id = -1;
            mMessageTask.add(dataSendTask);
            mMessageTask.notify();
        }
    }

    public void stopRuning() {
        mThreadStop = true;
    }

    @Override
    public void run() {
        if (mHeartbeatEnabled) {
            mHeartbeatThread = new HeartbeatThread(this);
            mHeartbeatThread.start();
        }
        mThreadStop = false;
        while (!mThreadStop) {
            synchronized (mMessageTask) {
                while (mMessageTask.isEmpty()) {
                    try {
                        mMessageTask.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SocketDataSendTask task = mMessageTask.remove(mMessageTask.size() - 1);
                Object dataToSend = task.dataToSend;
                MessageSendListener listener = task.listener;
                int id = task.id;
                Utils.logd(TAG, "start to send message id = " + id + " data=" + dataToSend);
                boolean success = false;

                try {
                    mOutputStream.writeObject(dataToSend);
                    mOutputStream.flush();
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    success = false;
                }
                if (listener != null) {
                    listener.onDataSendDone(id, success);
                }
            }
        }
        Utils.loge(TAG, "Exit the message send thread");
        if (mHeartbeatThread != null) {
            mHeartbeatThread.stop();
        }
    }
}
