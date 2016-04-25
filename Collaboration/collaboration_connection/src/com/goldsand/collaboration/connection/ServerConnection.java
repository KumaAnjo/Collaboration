
package com.goldsand.collaboration.connection;

import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;
import com.goldsand.collaboration.connection.SocketDataSenderThread.SocketDataSendTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

class ServerConnection implements Runnable {
    private static final String TAG = "Conncetion";
    private Socket mConnectionSocket = null;
    private ObjectInputStream mObjectInputStream = null;
    private ObjectOutputStream mObjectOutputStream = null;
    private ConnectStatus mConnectStatus = ConnectStatus.NOT_INIT;
    private String mRemoteIp;
    private SocketDataSenderThread mDataSenderThread;
    private boolean mStopConnection = false;
    private String mThreadName;
    private ConnectionThreadListener mConnectionListener = null;

    public ServerConnection(Socket connectionSocket, ConnectionThreadListener connectionThreadListener) {
        this.mConnectionSocket = connectionSocket;
        this.mConnectionListener = connectionThreadListener;
        mRemoteIp = Utils.changeIp(mConnectionSocket.getInetAddress().toString());
    }

    public void setThreadName(String threadName) {
        mThreadName = threadName;
    }

    public String getThreadName() {
        return mThreadName;
    }

    public void setAutorizeCheckPass() {
        mStopConnection = false;
        changeConnectStatus(ConnectStatus.CONNECTED);
    }

    /**
     * Can use this function to send data to Client
     * 
     * @param o
     * @throws Exception
     */
    public void sendDataToTarget(final Object o) {
        if (canSendData() && mDataSenderThread != null) {
            SocketDataSendTask task = new SocketDataSendTask(o, mMessageSendListener);
            int id = mDataSenderThread.addMessage(task);
            Utils.logd(TAG, "Data have been send to task, task id =" + id);
        }
    }

    private MessageSendListener mMessageSendListener = new MessageSendListener() {
        @Override
        public void onDataSendDone(int id, boolean success) {
            Utils.logd(TAG, "Data have been sended id=" + id + " success=" + success);
        }
    };

    public boolean canSendData() {
        return isConnected() || ConnectStatus.UNAUTHORIZED.equals(mConnectStatus);
    }

    /**
     * Get the connection status
     * 
     * @return if connected true else false
     */
    public boolean isConnected() {
        return ConnectStatus.CONNECTED.equals(mConnectStatus);
    }

    /**
     * Just stop the connection, but don't exit the thread.
     */
    public void stopConnection() {
        mStopConnection = true;
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = mConnectionSocket.getOutputStream();
            mObjectOutputStream = new ObjectOutputStream(outputStream);
            mDataSenderThread = new SocketDataSenderThread(getThreadName(), mObjectOutputStream, true);
            InputStream inputStream = mConnectionSocket.getInputStream();
            mObjectInputStream = new ObjectInputStream(inputStream);
            mDataSenderThread.start();
            changeConnectStatus(ConnectStatus.UNAUTHORIZED);
            mStopConnection = false;
            while (!mStopConnection) {
                try {
                    Object in = mObjectInputStream.readObject();
                    Utils.logd(TAG, "get message:" + in + " from:" + getThreadName());
                    mConnectionListener.onNewDataReceived(getThreadName(), in);
                } catch (SocketTimeoutException e) {
                    Utils.loge(TAG, "connection time out, discard connection");
                    mStopConnection = true;
                    releaseServerSocket();
                } catch (Exception e) {
                    e.printStackTrace();
                    mStopConnection = true;
                    releaseServerSocket();
                }
            }
            Utils.logd(TAG, "connection lost !!!");
            mRemoteIp = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            releaseServerSocket();
        }
    }

    private void changeConnectStatus(ConnectStatus connectStatus) {
        mConnectStatus = connectStatus;
        if (mConnectionListener != null) {
            mConnectionListener.onConnectionStatutChange(getThreadName(), mConnectStatus);
        }
    }

    public String getRemoteIp() {
        return mRemoteIp;
    }

    /*
     * Invoke when thread done. for Server Thread need override to release the
     * ServerSocket.
     */
    private void releaseServerSocket() {
        if (mObjectInputStream != null) {
            try {
                mObjectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mObjectInputStream = null;
        }
        if (mObjectOutputStream != null) {
            try {
                mObjectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mObjectOutputStream = null;
        }
        if (mConnectionSocket != null) {
            try {
                mConnectionSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mConnectionSocket = null;
        }
        if (mDataSenderThread != null) {
            mDataSenderThread.stopRuning();
        }
    }
}
