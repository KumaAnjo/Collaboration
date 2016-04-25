package com.goldsand.collaboration.server.manager;

import org.json.JSONObject;

public interface IMsgListener {
    public void onMessage(JSONObject dataObj);
}
