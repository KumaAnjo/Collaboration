package com.goldsand.collaboration.connection;

interface MessageSendListener {
    public void onDataSendDone(int id, boolean success);
}
