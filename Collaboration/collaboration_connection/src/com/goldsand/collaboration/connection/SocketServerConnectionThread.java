
package com.goldsand.collaboration.connection;

import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class SocketServerConnectionThread extends Thread implements ConnectionThreadListener {
    protected volatile boolean mStopRuning = false;
    private final String TAG;
    private int mConnectionPort;
    private String mRemoteIp;
    private ConnectionThreadListener mConnectionListener = null;
    private ConnectionThreads mConnectionThreads;
    private boolean mThreadRunning = false;
    private ConnectionListListener mConnectionListListener;

    public SocketServerConnectionThread(String name, int connectionPort, ConnectionListListener connectionListListener) {
        super(name);
        TAG = name;
        mConnectionPort = connectionPort;
        mConnectionListListener = connectionListListener;
        mConnectionThreads = ConnectionThreads.getInstance();
    }

    /**
     * Invoke when thread start. Override to change the running status Or other
     * init things. like create ServerSocket.
     */
    protected void initRunningArgument() {
        mStopRuning = false;
    }

    /**
     * disconnected the connection, and exit the thread.
     */
    public void stopRuning() {
        mStopRuning = true;
    }

    public boolean isThreadRunning() {
        return mThreadRunning;
    }

    /**
     * Set the message receive listener
     * 
     * @param listener
     */
    public void setNewMessageReceiveListener(ConnectionThreadListener listener) {
        mConnectionListener = listener;
    }

    private Map<String, ServerConnection> mConnectionMap = new HashMap<String, ServerConnection>();

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(mConnectionPort);
            while (!mStopRuning) {
                mThreadRunning = true;
                // We just need one connection.So if connected , we will not
                // continue to accept new socket
                Socket connectionSocket = serverSocket.accept();
                connectionSocket.setSoTimeout(HeartbeatThread.HEARTHEAT_TIME_OUT);
                ServerConnection connection = new ServerConnection(connectionSocket, this);
                String threadName = mConnectionThreads.startRun(connection);
                mConnectionListListener.onNewConnection(threadName, connectionSocket
                        .getInetAddress().toString());
                synchronized (mConnectionMap) {
                    mConnectionMap.put(threadName, connection);
                }
                Utils.logd(TAG, "New client accept ip = " + mRemoteIp);
                // Client disconnected, stop the sender thread ,and create new
                // one.
            }
            mThreadRunning = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Utils.logd(TAG, "bey bey! Exit the accept thread");
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ServerConnection getConnection(String threadName) {
        synchronized (mConnectionMap) {
            ServerConnection connection = mConnectionMap.get(threadName);
            if (connection == null) {
                Utils.loge(TAG, "connection with " + threadName + " is null");
            }
            return connection;
        }
    }

    public void sendDataToTarget(Object data) {
        synchronized (mConnectionMap) {
            Set<String> keys = mConnectionMap.keySet();
            for (String key : keys) {
                mConnectionMap.get(key).sendDataToTarget(data);
            }
        }
    }

    public void sendDataToTarget(String targetThread, Object data) {
        if (Utils.isEmpty(targetThread)) {
            sendDataToTarget(data);
        } else if (getConnection(targetThread) != null) {
            getConnection(targetThread).sendDataToTarget(data);
        }
    }

    public void stopConnection(String threadName) {
        if (getConnection(threadName) != null) {
            getConnection(threadName).stopConnection();
        }
    }

    public void setAutorizeCheckPass(String threadName) {
        if (getConnection(threadName) != null) {
            getConnection(threadName).setAutorizeCheckPass();
        }
    }

    @Override
    public void onNewDataReceived(String threadName, Object data) {
        this.mConnectionListener.onNewDataReceived(threadName, data);
    }

    @Override
    public void onConnectionStatutChange(String threadName, ConnectStatus newStatus) {
        if (ConnectStatus.DISCONNECTED.equals(newStatus)) {
            synchronized (mConnectionMap) {
                mConnectionMap.remove(threadName);
                mConnectionListListener.onRemoveConnection(threadName);
            }
        }
        this.mConnectionListener.onConnectionStatutChange(threadName, newStatus);
    }

}
