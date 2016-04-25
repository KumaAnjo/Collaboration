
package com.goldsand.collaboration.connection;

import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;

interface ConnectionThreadListener {
    public void onNewDataReceived(String threadName, Object data);
    public void onConnectionStatutChange(String threadName, ConnectStatus newStatus);
}
