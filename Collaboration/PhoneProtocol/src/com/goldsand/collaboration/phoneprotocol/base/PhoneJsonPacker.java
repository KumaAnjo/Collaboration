package com.goldsand.collaboration.phoneprotocol.base;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * convert phone object to Json string
 *
 * @return JSONObject
 * @author ping.zhang
 * @since 12/21/2014
 */
public class PhoneJsonPacker {
    /**
     * pack the {@see PhoneJson.java} object to JSON data
     *
     * @param phoneJson PhoneJson object
     * @return JSON data
     */
    public JSONObject getPhoneJsonObject(PhoneJson phoneJson) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(PhoneJSONConstant.SUB_VERSION, phoneJson.getSubVersion());
            jsonObj.put(PhoneJSONConstant.MODULE, phoneJson.getModule());
            jsonObj.put(PhoneJSONConstant.CARD_SLOT, phoneJson.getCardSlot());
            jsonObj.put(PhoneJSONConstant.CALL_TYPE, phoneJson.getCallType());
            jsonObj.put(PhoneJSONConstant.CONNECT, phoneJson.getConnect());
            jsonObj.put(PhoneJSONConstant.STATUS, phoneJson.getStatus());
            jsonObj.put(PhoneJSONConstant.NAME, phoneJson.getName());
            jsonObj.put(PhoneJSONConstant.NUMBER, phoneJson.getNumber());
            jsonObj.put(PhoneJSONConstant.SERIAL_NO, phoneJson.getSerialno());
        } catch (JSONException ex) {
            ex.printStackTrace();
            jsonObj = null;
        }

        return jsonObj;
    }
}
