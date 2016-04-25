
package com.goldsand.collaboration.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;
import com.goldsand.collaboration.connection.SocketDataSenderThread.SocketDataSendTask;

class SocketClientConnectionThread extends Thread {
    protected volatile boolean mStopRuning = false;
    protected volatile boolean mStopConnection = false;
    private Socket mConnectionSocket = null;
    private ObjectInputStream mObjectInputStream = null;
    private ObjectOutputStream mObjectOutputStream = null;
    private final String TAG;
    private ConnectStatus mConnectStatus = ConnectStatus.NOT_INIT;
    private ConnectionThreadListener mConnectionListener = null;
    private int mServerPort;
    private String mServerIp;
    private SocketDataSenderThread mDataSenderThread;

    public SocketClientConnectionThread(String name) {
        super(name);
        TAG = name;
    }

    public String getServerIp() {
        return mServerIp;
    }

    public void startConnection(int port, String serverIp) {
        this.mServerPort = port;
        this.mServerIp = serverIp;
        start();
    }

    /**
     * Invoke when thread start. Override to change the running status Or other
     * init things. like create ServerSocket.
     */
    protected void initRunningArgument() {
        mStopRuning = true;
        mStopConnection = false;
    }

    /*
     * Invoke when thread done. for Server Thread need override to release the
     * ServerSocket.
     */
    protected void releaseServerSocket() {
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

    /**
     * Get the connection status
     * 
     * @return if connected true else false
     */
    public boolean isConnected() {
        return ConnectStatus.CONNECTED.equals(mConnectStatus);
    }

    public boolean disConnected() {
        return ConnectStatus.DISCONNECTED.equals(mConnectStatus);
    }

    public boolean canSendData() {
        return isConnected() || ConnectStatus.UNAUTHORIZED.equals(mConnectStatus);
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

    public void sendHeartbeatPackages() {
        if (isConnected() && mDataSenderThread != null) {
            mDataSenderThread.addHeartbeatPackage();
        }
    }

    private MessageSendListener mMessageSendListener = new MessageSendListener() {
        @Override
        public void onDataSendDone(int id, boolean success) {
            Utils.logd(TAG, "Data have been sended id=" + id + " success=" + success);
        }
    };

    /**
     * Just stop the connection, but don't exit the thread.
     */
    public void stopConnection() {
        mStopConnection = true;
    }

    /**
     * disconnected the connection, and exit the thread.
     */
    public void stopRuning() {
        mStopConnection = true;
        mStopRuning = true;
    }

    /**
     * Set the message receive listener
     * 
     * @param listener
     */
    public void setNewMessageReceiveListener(ConnectionThreadListener listener) {
        mConnectionListener = listener;
    }

    public void run() {
        initRunningArgument();
        try {
            // We just need one connection.So if connected , we will not
            // continue to accept new socket
            Utils.logd(TAG, "start to connected to ip" + mServerIp + " prot:" + mServerPort);
            mConnectionSocket = new Socket(mServerIp, mServerPort);
            mConnectionSocket.setSoTimeout(HeartbeatThread.HEARTHEAT_TIME_OUT);
            InputStream inputStream = mConnectionSocket.getInputStream();
            mObjectInputStream = new ObjectInputStream(inputStream);
            OutputStream outputStream = mConnectionSocket.getOutputStream();
            mObjectOutputStream = new ObjectOutputStream(outputStream);
            mDataSenderThread = new SocketDataSenderThread(mObjectOutputStream, false);
            mDataSenderThread.start();
            Utils.logd(TAG, "conected to " + mServerIp);
            if (mConnectionSocket == null) {
                Utils.loge(TAG, " null socket");
                return;
            }
            try {
                changeConnectStatus(ConnectStatus.UNAUTHORIZED);
                while (!mStopConnection) {
                    Object in = mObjectInputStream.readObject();
                    Utils.logd(TAG, "get message:" + in);
                    if (in instanceof InnerPackage
                            && !InnerPackage.isHeatbeatPackage((InnerPackage) in)) {
                        String message = ((InnerPackage)in).getMessage();
                        if (InnerPackage.PASS.equals(message)) {
                            changeConnectStatus(ConnectStatus.CONNECTED);
                        } else if (InnerPackage.FAILED.equals(message)) {
                            stopConnection();
                            Utils.loge(TAG, "authorize failed!! exit this thread.");
                        }
                    } else if (mConnectionListener != null) {
                        mConnectionListener.onNewDataReceived(getName(), in);
                    }
                }
            } catch (SocketTimeoutException e) {
                Utils.loge(TAG, "receive data time out.");
            }
            sendDataToTarget("beybey! close by server");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            changeConnectStatus(ConnectStatus.DISCONNECTED);
            Utils.loge(TAG, "bey bey!");
            releaseServerSocket();
        }
    }

    private void changeConnectStatus(ConnectStatus connectStatus) {
        mConnectStatus = connectStatus;
        if (mConnectionListener != null) {
            mConnectionListener.onConnectionStatutChange(null, mConnectStatus);
        }
    }
}
