package com.goldsand.collaboration.connection;

public interface Authorizer {
    public boolean authorize(String remoteIp);
}
