package com.goldsand.collaboration.commonprotocol;

import org.json.JSONObject;

public class CommonModuleCombine {
    private CommonJson mCommObj;
    private JSONObject mModuleJSONObj;

    public CommonJson getCommObj() {
        return mCommObj;
    }

    public void setCommObj(CommonJson obj) {
        this.mCommObj = obj;
    }

    public JSONObject getModuleJSONObj() {
        return mModuleJSONObj;
    }

    public void setModuleJSONObj(JSONObject obj) {
        this.mModuleJSONObj = obj;
    }
}
