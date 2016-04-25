package com.goldsand.collaboration.connection;

import java.util.List;

public interface ConnectionInterface {
    public void start();
    public void stop();
    public void registConnectionListener(ConnectionListener listener);
    public boolean sendDataToTarget(Object object);
    public NetWorkInfo getNetWorkInfo();
    public List<NetWorkInfo> getNetWorkInfos();
}
