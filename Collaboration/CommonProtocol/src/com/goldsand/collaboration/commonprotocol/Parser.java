package com.goldsand.collaboration.commonprotocol;

import org.json.JSONObject;

public interface Parser {
    /**
     * To parse module JSON data
     *
     * @param moduleObj module JSON object
     * @return module object
     */
    public BaseJson parser(JSONObject moduleObj);
}
