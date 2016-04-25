
package com.goldsand.collaboration.connection;

public class NetWorkInfo {
    public enum ConnectStatus {
        NOT_INIT,
        UNAUTHORIZED,
        CONNECTED,
        ERROR_CLIENT_BROADCAST_CONFLICT,
        ERROR_SERVER_BROADCAST_CONFLICT,
        ERROR_CONNECTION_CONFLICT,
        ERROR_EXCEPTION, DISCONNECTED
    };

    private ConnectStatus mStatus;
    private int mConnectionNumber;
    private String mLocalIp;
    private String mRemoteIp;
    private int mClientBroadcastPort;
    private int mServerBroadcastPort;
    private int mConnectionPort;

    public NetWorkInfo() {
        this.mStatus = ConnectStatus.NOT_INIT;
        this.mConnectionNumber = 0;
        this.mRemoteIp = null;
        this.mClientBroadcastPort = -1;
        this.mServerBroadcastPort = -1;
        this.mConnectionPort = -1;
    }

    public ConnectStatus getStatus() {
        return mStatus;
    }

    void setStatus(ConnectStatus status) {
        this.mStatus = status;
    }

    public int getConnectionNumber() {
        return mConnectionNumber;
    }

    void setConnectionNumber(int mConnectionNumber) {
        this.mConnectionNumber = mConnectionNumber;
    }

    public String getLocalIp() {
        return mLocalIp;
    }

    void setLocalIp(String localIp) {
        this.mLocalIp = localIp;
    }

    public String getRemoteIp() {
        return mRemoteIp;
    }

    void setRemoteIp(String remoteIp) {
        this.mRemoteIp = remoteIp;
    }


    public int getClientBroadcastPort() {
        return mClientBroadcastPort;
    }

    void setClientBroadcastPort(int clientBroadcastPort) {
        this.mClientBroadcastPort = clientBroadcastPort;
    }

    public int getServerBroadcastPort() {
        return mServerBroadcastPort;
    }

    void setServerBroadcastPort(int serverBroadcastPort) {
        this.mServerBroadcastPort = serverBroadcastPort;
    }

    public int getConnectionPort() {
        return mConnectionPort;
    }

    void setConnectionPort(int connectionPort) {
        this.mConnectionPort = connectionPort;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("NetWork info:\n");
        stringBuilder.append("mStatus:" + mStatus + "\n");
        stringBuilder.append("mConnectionNumber:" + mConnectionNumber + "\n");
        stringBuilder.append("mLocalIp:" + mLocalIp + "\n");
        stringBuilder.append("mRemoteIp:" + mRemoteIp + "\n");
        stringBuilder.append("mClientBroadcastPort:" + mClientBroadcastPort
                + "\tmServerBroadcastPort=" + mServerBroadcastPort + "\t mConnectionPort="
                + mConnectionPort + "\n");
        return stringBuilder.toString();
    }

}
