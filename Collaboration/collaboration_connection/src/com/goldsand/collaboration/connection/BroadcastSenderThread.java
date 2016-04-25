
package com.goldsand.collaboration.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class BroadcastSenderThread extends Thread {
    public static class BroadcastTask {
        private int id;
        private int port;
        private String message;
        private MessageSendListener listener;
        public BroadcastTask(int port, String message, MessageSendListener listener) {
            this.port = port;
            this.message = message;
            this.listener = listener;
        }
    }

    private static final String TAG = "BroadcastSenderThread";
    private List<BroadcastTask> mBroadcastTasks = new ArrayList<BroadcastTask>();
    private int mCurrentId = 0;
    private volatile boolean mThreadStop = false;
    private volatile boolean mThreadRunning = false;

    public int addBroadcastTask(BroadcastTask broadcastTask) {
        synchronized (mBroadcastTasks) {
            mCurrentId++;
            broadcastTask.id = mCurrentId;
            mBroadcastTasks.add(broadcastTask);
            mBroadcastTasks.notify();
            return mCurrentId;
        }
    }

    public void stopThread() {
        mThreadStop = true;
    }

    public boolean isThreadRunning() {
        return mThreadRunning;
    }

    @Override
    public void run() {
        while (!mThreadStop) {
            mThreadRunning = true;
            synchronized (mBroadcastTasks) {
                while (mBroadcastTasks.isEmpty()) {
                    try {
                        mBroadcastTasks.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                BroadcastTask current = mBroadcastTasks.remove(mBroadcastTasks.size() - 1);
                sendBroadcast(current);
            }
        }
        mThreadRunning = false;
    }

    private void sendBroadcast(BroadcastTask task) {
        DatagramSocket client = null;
        String message = task.message;
        int port = task.port;
        int id = task.id;
        MessageSendListener listener = task.listener;
        boolean sendSuccess = false;
        try {
            Utils.logd(TAG, "Start to send broadcast id=" + id + " message = " + message);
            byte[] msg = message.getBytes();
            InetAddress inetAddr = InetAddress.getByName("255.255.255.255");
            client = new DatagramSocket();
            DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddr,
                    port);
            client.send(sendPack);
            Utils.logd(TAG, " Net broadcast send done on port:" + port);
            sendSuccess = true;
        } catch (SocketException e) {
            e.printStackTrace();
            sendSuccess = false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            sendSuccess = false;
        } catch (IOException e) {
            e.printStackTrace();
            sendSuccess = false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
        if (listener != null) {
            listener.onDataSendDone(id, sendSuccess);
        }
    }
}
