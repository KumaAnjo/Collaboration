package com.goldsand.collaboration.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

class BroadcastReceiveThread extends Thread{
    private final String TAG;
    private DatagramSocket mClientRecevieSocket = null;
    private int mListenPort = -1;
    private static final int MAXREV = 255;
    //Mark this thread is running or not.
    private boolean mThreadRuning = false;
    //Control this thread running or exit.
    private volatile boolean mStopReceive = false;
    private ConnectionThreadListener mConnectionListener = null;

    public BroadcastReceiveThread(int port) {
        super("client_receive_thread_port_"+port);
        mListenPort = port;
        TAG = "BroadcastReceiveThread:" + port;
    }

    public boolean isThreadRuning() {
        return mThreadRuning;
    }

    public boolean isReceiverStoped() {
        return mStopReceive;
    }

    public void stopReceive() {
        mStopReceive = true;
        if (mClientRecevieSocket != null) {
            mClientRecevieSocket.close();
        }
    }

    public void setMessageListener(ConnectionThreadListener listener) {
        mConnectionListener = listener;
    }

    @Override
    public void run() {
        try {
            mClientRecevieSocket = new DatagramSocket(mListenPort);
            DatagramPacket recvPacket = new DatagramPacket(new byte[MAXREV], MAXREV);
            mThreadRuning = true;
            mStopReceive = false;
            while (!mStopReceive)
            {
                mClientRecevieSocket.receive(recvPacket);
                byte[] receiveMsg = Arrays.copyOfRange(recvPacket.getData(),
                        recvPacket.getOffset(),
                        recvPacket.getOffset() + recvPacket.getLength());
                Utils.logd(TAG, "Handing at client "
                        + recvPacket.getAddress().getHostName() + " ip "
                        + recvPacket.getAddress().getHostAddress());
                String receive = new String(receiveMsg);
                if (mConnectionListener != null) {
                    mConnectionListener.onNewDataReceived(getName(), receive);
                }
                Utils.logd(TAG, "Server Receive Data:" + receive);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.logd(TAG, " client boradcast receive thread exited");
            if (mClientRecevieSocket != null && !mClientRecevieSocket.isClosed()) {
                mClientRecevieSocket.close();
                mClientRecevieSocket = null;
            }
            mThreadRuning = false;
        }
    }

}
