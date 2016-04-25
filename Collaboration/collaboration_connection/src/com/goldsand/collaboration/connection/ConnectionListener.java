package com.goldsand.collaboration.connection;

import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;

public interface ConnectionListener {
    public void onNewDataReceive(Object data);
    public void onNewDataReceive(String data);
    public void onConnectionStatusChanged(ConnectStatus newStatus);
}
