package com.goldsand.collaboration.commonprotocol;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonPacker {

    /**
     * pack to json object according {@see CommonJson} and sub JSON object
     *
     * @param commJson CommonJson object
     *        moduleObj sub module json object
     * @return String transferred to network peer
     */
    public String packToJson(CommonJson commJson, JSONObject moduleObj) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(CommonJSONConstant.VERSION, commJson.getVersion());
            jsonObj.put(CommonJSONConstant.MODULE, commJson.getModule());
            jsonObj.put(CommonJSONConstant.MODULE_OBJ, moduleObj);

            // exception check
            if (!commJson.getModule().equalsIgnoreCase(moduleObj.optString(CommonJSONConstant.MODULE))) {
                throw new ModuleMismatchException("module name is mismatch");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            jsonObj = null;
        }

        if (jsonObj != null) {
          return jsonObj.toString();
        } else {
            return null;
        }
    }
}
