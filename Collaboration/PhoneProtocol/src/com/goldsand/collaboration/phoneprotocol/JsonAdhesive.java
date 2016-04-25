package com.goldsand.collaboration.phoneprotocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.goldsand.collaboration.phoneprotocol.Module;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJson;
import com.goldsand.collaboration.phoneprotocol.base.PhoneJsonPacker;

import com.goldsand.collaboration.commonprotocol.CommonJson;
import com.goldsand.collaboration.commonprotocol.JsonPacker;
import com.goldsand.collaboration.commonprotocol.JsonParser;

public class JsonAdhesive {
    /**
     * convert PhoneJson object to network string
     *
     * @param jsonObj PhoneJson object
     * @return
     */
    public static String packJson(PhoneJson jsonObj) {
        PhoneJsonPacker phoneJson = new PhoneJsonPacker();
        JSONObject phoneJsonObj = phoneJson.getPhoneJsonObject(jsonObj);

        CommonJson commJsonObj = new CommonJson();
        commJsonObj.setModule(Module.PHONE);
        commJsonObj.setModuleObj(jsonObj);

        JsonPacker commJson = new JsonPacker();
        return commJson.packToJson(commJsonObj, phoneJsonObj);
    }

    /**
     * convert network string to PhoneJson object
     *
     * @param from
     * @return
     */
    public static PhoneJson parserPhoneJson(String from) {
        JsonParser jsonParser = new JsonParser();
        PhoneJson phoneJsonObj = null;
        try {
            CommonJson commJsonObj = (jsonParser.parse(from)).getCommObj();
            phoneJsonObj = (PhoneJson)commJsonObj.getModuleObj();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return phoneJsonObj;
    }
}
