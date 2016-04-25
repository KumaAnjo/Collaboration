package com.goldsand.collaboration.commonprotocol;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    public void JsonParser() {
        // null
    }

    /**
     * parser JSON data
     *
     * @param jsonString
     * @return a combine that contain {@see CommonJson.java} and {@see JSONObject.obj}
     * @throws JSONException
     */
    public CommonModuleCombine parse(String jsonString) throws JSONException {
        CommonModuleCombine combineObj = new CommonModuleCombine();
        CommonJson commObj = new CommonJson();
        combineObj.setCommObj(commObj);

        JSONObject json = new JSONObject(jsonString);

        commObj.setVersion(json.optString(CommonJSONConstant.VERSION));
        commObj.setModule(json.optString(CommonJSONConstant.MODULE));

        JSONObject baseJson = json.optJSONObject(CommonJSONConstant.MODULE_OBJ);
        combineObj.setModuleJSONObj(baseJson);

        return combineObj;
    }
}
