package com.goldsand.collaboration.client.manager;

import org.json.JSONObject;

public interface IMsgListener {
    public void onMessage(JSONObject dataObj);
}
