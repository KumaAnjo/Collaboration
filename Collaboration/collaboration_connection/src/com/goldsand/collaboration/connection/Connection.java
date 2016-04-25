
package com.goldsand.collaboration.connection;

import com.goldsand.collaboration.connection.BroadcastSenderThread.BroadcastTask;
import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Connection implements ConnectionInterface, ConnectionThreadListener,
        ConnectionListListener {
    public enum ConnectionType {
        TYPE_SERVER, TYPE_CLIENT
    }

    private static final String TAG = "Connection";
    private final int SERVER_PORT;
    private final int CLIENT_BROADCAST_THREAD_PORT;
    private final int SERVER_PROADCAST_THREAD_PORT;
    private static final String CLIENT_CONNECTION_THREAD_NAME = "client_connection_thread";

    private BroadcastReceiveThread mBroadcastReceiveThread = null;
    private SocketClientConnectionThread mSocketClientThread = null;
    private BroadcastSenderThread mBroadcastSenderThread = null;
    private SocketServerConnectionThread mSocketServerThread = null;
    private String mServerIp;
    private int mServerPort;
    private ConnectionListener mConnectionListener = null;
    private NetWorkInfo mNetWorkInfo = null;
    private ConnectionType mConnctionType;
    private boolean mNeedCheckAuthorize;
    private Authorizer mAuthorizer;
    private Map<String, NetWorkInfo> mNetWorkInfos = new HashMap<String, NetWorkInfo>();

    public Connection(ConnectionType type) {
        this(type, null);
    }

    public Connection(ConnectionType type, Authorizer authorizer) {
        if (authorizer == null) {
            mNeedCheckAuthorize = false;
        } else {
            mNeedCheckAuthorize = true;
            mAuthorizer = authorizer;
        }
        mConnctionType = type;
        SERVER_PORT = getConnectionPort();
        CLIENT_BROADCAST_THREAD_PORT = getClientBroadcastPort();
        SERVER_PROADCAST_THREAD_PORT = getServerBroadcastPort();
        mNetWorkInfo = new NetWorkInfo();
        mNetWorkInfo.setConnectionPort(SERVER_PORT);
        mNetWorkInfo.setClientBroadcastPort(CLIENT_BROADCAST_THREAD_PORT);
        mNetWorkInfo.setServerBroadcastPort(SERVER_PROADCAST_THREAD_PORT);
        mNetWorkInfo.setLocalIp(getLocalHostIp());
        // Check port status
        checkPort();
    }

    private NetWorkInfo createGenerat() {
        NetWorkInfo netWorkInfo = new NetWorkInfo();
        netWorkInfo.setConnectionPort(SERVER_PORT);
        netWorkInfo.setClientBroadcastPort(CLIENT_BROADCAST_THREAD_PORT);
        netWorkInfo.setServerBroadcastPort(SERVER_PROADCAST_THREAD_PORT);
        netWorkInfo.setLocalIp(getLocalHostIp());
        return netWorkInfo;
    }

    @Override
    public void onNewConnection(String thread, String remoteIp) {
        synchronized (mNetWorkInfos) {
            NetWorkInfo netWorkInfo = createGenerat();
            netWorkInfo.setRemoteIp(Utils.changeIp(remoteIp));
            mNetWorkInfos.put(thread, netWorkInfo);
        }
    }

    @Override
    public void onRemoveConnection(String thread) {
        synchronized (mNetWorkInfos) {
            mNetWorkInfos.remove(thread);
        }
    }

    private void init() {
        if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
            mBroadcastReceiveThread = new BroadcastReceiveThread(CLIENT_BROADCAST_THREAD_PORT);
            mSocketServerThread = new SocketServerConnectionThread("socket_connection_thread",
                    SERVER_PORT, this);
            mSocketServerThread.setNewMessageReceiveListener(this);
        } else {
            mBroadcastReceiveThread = new BroadcastReceiveThread(SERVER_PROADCAST_THREAD_PORT);
            mSocketClientThread = new SocketClientConnectionThread(CLIENT_CONNECTION_THREAD_NAME);
            mSocketClientThread.setNewMessageReceiveListener(this);
        }
        mBroadcastReceiveThread.setMessageListener(this);

        mBroadcastSenderThread = new BroadcastSenderThread();
        mBroadcastSenderThread.start();
    }

    private boolean checkPort() {
        if (!Utils.checkPort(SERVER_PORT)) {
            mNetWorkInfo.setStatus(ConnectStatus.ERROR_CONNECTION_CONFLICT);
            return false;
        }
        if (!Utils.checkPort(CLIENT_BROADCAST_THREAD_PORT)) {
            mNetWorkInfo.setStatus(ConnectStatus.ERROR_CLIENT_BROADCAST_CONFLICT);
            return false;
        }
        if (!Utils.checkPort(SERVER_PROADCAST_THREAD_PORT)) {
            mNetWorkInfo.setStatus(ConnectStatus.ERROR_SERVER_BROADCAST_CONFLICT);
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionStatutChange(String threadName, ConnectStatus newStatus) {
        Utils.logd(TAG, "onConnectionStatutChange threadName = " + threadName + " newStatus = "
                + newStatus + " mConnectionMap=" + mNetWorkInfos);

        if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
            synchronized (mNetWorkInfos) {
                if (mNetWorkInfos.get(threadName) != null) {
                    mNetWorkInfos.get(threadName).setStatus(newStatus);
                }
            }
        } else {
            synchronized (mNetWorkInfos) {
                mNetWorkInfo.setStatus(newStatus);
                String remoteIp = null;
                remoteIp = mSocketClientThread.getServerIp();
                mNetWorkInfo.setRemoteIp(remoteIp);
            }
        }

        if (mConnectionListener != null) {
            mConnectionListener.onConnectionStatusChanged(newStatus);
        }

        // Send authorize check
        if (ConnectStatus.UNAUTHORIZED.equals(newStatus)) {
            if (ConnectionType.TYPE_CLIENT.equals(mConnctionType)) {
                if (!checkDeviceNameAndMac()) {
                    String deviceId = Utils.createDeviceInfo(getDeviceName(), getDeviceMac());
                    InnerPackage innerPackage = InnerPackage.createAuthorizePackage(deviceId);
                    sendDataToTargetInner(threadName, innerPackage);
                }
            }
        }
    }

    private boolean checkDeviceNameAndMac() {
        if (Utils.isEmpty(getDeviceName()) || Utils.isEmpty(getDeviceMac())) {
            Utils.loge(
                    TAG,
                    "deviceName and mac can be null in client override getDeviceName() and getDeviceMac to resolve this");
            return true;
        }
        return false;
    }

    public abstract int getServerBroadcastPort();

    public abstract int getClientBroadcastPort();

    public abstract int getConnectionPort();

    public abstract String getLocalHostIp();

    protected String getDeviceName() {
        return null;
    }

    protected String getDeviceMac() {
        return null;
    }

    /**
     * Override to set need check authorize or not.
     * 
     * @return
     */
    private boolean needCheckAuthorize() {
        /* do not need check authorize as default. */
        return mNeedCheckAuthorize;
    }

    private void startClient() {
        // Setup 1, Listen to the server broadcast
        if (!mBroadcastReceiveThread.isThreadRuning()) {
            mBroadcastReceiveThread.start();
        }
        // Setup 2, Send client broadcast to server.
        mBroadcastSenderThread.addBroadcastTask(new BroadcastTask(CLIENT_BROADCAST_THREAD_PORT,
                "New_Client_In", mMessageSendListener));
    }

    private void startServer() {
        // Setup1. Start socket server thread. wait for connection.
        Utils.logd(TAG, " ServerIn socketServerThread isAlive = " + mSocketServerThread.isAlive()
                + " connected=" + mSocketServerThread.isThreadRunning());
        if (!mSocketServerThread.isAlive() && !mSocketServerThread.isThreadRunning()) {
            mSocketServerThread.start();
        }

        Utils.logd(TAG, " ServerIn mClientBroadCastReceiverThread isAlive = "
                + mBroadcastReceiveThread.isAlive()
                + " connected=" + mBroadcastReceiveThread.isThreadRuning());
        // Setup2. Start listening to client broadcast.
        if (!mBroadcastReceiveThread.isAlive()) {
            mBroadcastReceiveThread.start();
        }

        // Setup3. Send broadcast to client, tell the client the server's ip
        // and port.
        String currentIp = null;
        try {
            currentIp = getLocalHostIp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // new BroadcastSenderTask(SERVER_PROADCAST_THREAD_PORT, currentIp
        // + ":" + SERVER_PORT, null, this).start();
        mBroadcastSenderThread.addBroadcastTask(new BroadcastTask(SERVER_PROADCAST_THREAD_PORT,
                currentIp + ":" + SERVER_PORT, mMessageSendListener));
    }

    private void stopClient() {
        if (mSocketClientThread != null && mSocketClientThread.isConnected()) {
            mSocketClientThread.stopRuning();
        }
    }

    private void stopServer() {
        if (mSocketServerThread != null && mSocketServerThread.isThreadRunning()) {
            mSocketServerThread.stopRuning();
        }
    }

    public MessageSendListener mMessageSendListener = new MessageSendListener() {
        @Override
        public void onDataSendDone(int id, boolean success) {
            Utils.logd(TAG, "Broadcast send done id=" + id + " success=" + success);
        }
    };

    private void handleAuthorizePackage(String threadName, InnerPackage in) {
        String message = ((InnerPackage) in).getMessage();
        if (needCheckAuthorize()) {
            if (mAuthorizer != null) {
                boolean authorize = mAuthorizer.authorize(message);
                if (!authorize) {
                    sendAuthorizePackage(threadName, InnerPackage.FAILED);
                    mSocketServerThread.stopConnection(threadName);
                } else {
                    sendAuthorizePackage(threadName, InnerPackage.PASS);
                    mSocketServerThread.setAutorizeCheckPass(threadName);
                }
            } else {
                throw new RuntimeException("If need check authorize, the Authorizer can't be null");
            }
        } else {
            mSocketServerThread.setAutorizeCheckPass(threadName);
        }
    }

    private void sendAuthorizePackage(String threadName, String result) {
        InnerPackage innerPackage = new InnerPackage(result);
        sendDataToTargetInner(threadName, innerPackage);
    }

    @Override
    public void onNewDataReceived(String threadName, Object data) {
        Utils.logd(TAG, "Receive data from " + threadName + " data:" + data);
        if (Utils.equals(threadName, mBroadcastReceiveThread.getName())) {
            if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
                // Receive UDP Broadcast from Client. Send a broadcast to tell
                // client the server IP and port.
                String currentIp = null;
                try {
                    currentIp = getLocalHostIp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBroadcastSenderThread.addBroadcastTask(new BroadcastTask(
                        SERVER_PROADCAST_THREAD_PORT,
                        currentIp + ":" + SERVER_PORT, mMessageSendListener));
            } else {
                String messageStr = (String) data;
                mServerIp = messageStr.substring(0, messageStr.indexOf(":"));
                mServerPort = Integer.parseInt(messageStr.substring(messageStr.indexOf(":") + 1,
                        messageStr.length()));
                if (mSocketClientThread != null && mSocketClientThread.isConnected()) {
                    // Thread is connected. Do nothing.
                    return;
                } else if ((mSocketClientThread != null && !mSocketClientThread.isConnected()
                        && !mSocketClientThread.isInterrupted()) || mSocketClientThread == null) {
                    // Thread is running, and hold by socket connection Or
                    // thread is
                    // null.Replace with new thread.
                    mSocketClientThread = new SocketClientConnectionThread(
                            CLIENT_CONNECTION_THREAD_NAME);
                    mSocketClientThread.setNewMessageReceiveListener(this);
                }
                mSocketClientThread.startConnection(mServerPort, mServerIp);
            }
        } else {
            if (mConnectionListener != null) {
                if (data instanceof InnerPackage) {
                    if (InnerPackage.isHeatbeatPackage((InnerPackage) data)) {
                        // When client get a heartbeat package ,return to Server
                        if (!ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
                            mSocketClientThread.sendHeartbeatPackages();
                        } else {
                            // Do nothing
                        }
                    } else {
                        handleAuthorizePackage(threadName, (InnerPackage) data);
                    }
                } else if (data instanceof String) {
                    mConnectionListener.onNewDataReceive((String) data);
                } else {
                    // If connection not connected , will not pass the data to
                    // client.
                    if (!ConnectStatus.CONNECTED.equals(mNetWorkInfo.getStatus())) {
                        mConnectionListener.onNewDataReceive(data);
                    }
                }
            }
        }
    }

    @Override
    public void start() {
        if (ConnectStatus.CONNECTED.equals(mNetWorkInfo.getStatus())) {
            Utils.logd(TAG, "already connected!!");
            return;
        }
        init();
        if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
            // Type server
            startServer();
        } else {
            // Type client
            startClient();
        }
    }

    @Override
    public void stop() {
        if (!ConnectStatus.CONNECTED.equals(mNetWorkInfo.getStatus())) {
            Utils.logd(TAG, "not connected!!");
            return;
        }
        if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
            stopServer();
        } else {
            stopClient();
        }
        if (mBroadcastReceiveThread != null && mBroadcastReceiveThread.isThreadRuning()) {
            mBroadcastReceiveThread.stopReceive();
        }
        if (mBroadcastSenderThread != null) {
            mBroadcastSenderThread.stopThread();
            mBroadcastSenderThread = null;
        }
    }

    @Override
    public void registConnectionListener(ConnectionListener listener) {
        this.mConnectionListener = listener;
    }

    private boolean sendDataToTargetInner(String targetThread, Object object) {
        if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
            if (mSocketServerThread == null || !mSocketServerThread.isThreadRunning()) {
                Utils.loge(TAG, "Socket server thread is null or socket server not connected");
                return false;
            } else {
                mSocketServerThread.sendDataToTarget(targetThread, object);
                return true;
            }
        } else {
            if (mSocketClientThread == null || !mSocketClientThread.canSendData()) {
                return false;
            } else {
                mSocketClientThread.sendDataToTarget(object);
                return true;
            }
        }
    }

    @Override
    public boolean sendDataToTarget(Object object) {
        if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
            if (mSocketServerThread == null || !mSocketServerThread.isThreadRunning()) {
                Utils.loge(TAG, "Socket server thread is null or socket server not connected");
                return false;
            } else {
                mSocketServerThread.sendDataToTarget(object);
                return true;
            }
        } else {
            if (mSocketClientThread == null || !mSocketClientThread.isConnected()) {
                return false;
            } else {
                mSocketClientThread.sendDataToTarget(object);
                return true;
            }
        }
    }

    @Override
    public NetWorkInfo getNetWorkInfo() {
        //For server, always return the first connection. 
        if (ConnectionType.TYPE_SERVER.equals(mConnctionType)) {
            synchronized (mNetWorkInfos) {
                return mNetWorkInfos == null ? null : mNetWorkInfos.get(0);
            }
        }
        synchronized (mNetWorkInfo) {
            return mNetWorkInfo;
        }
    }

    @Override
    public List<NetWorkInfo> getNetWorkInfos() {
        synchronized (mNetWorkInfos) {
            ArrayList<NetWorkInfo> networkInfos = new ArrayList<NetWorkInfo>();
            networkInfos.addAll(mNetWorkInfos.values());
            return networkInfos;
        }
    }

}
