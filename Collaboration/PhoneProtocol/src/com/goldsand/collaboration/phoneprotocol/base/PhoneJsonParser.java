package com.goldsand.collaboration.phoneprotocol.base;

import org.json.JSONObject;

import com.goldsand.collaboration.commonprotocol.Parser;
import com.goldsand.collaboration.phoneprotocol.Module;

/*
 * convert Json string to phone object
 *
 * @return PhoneJson
 * @author ping.zhang
 * @since 12/21/2014
 */
public class PhoneJsonParser implements Parser {

    @Override
    public PhoneJson parser(JSONObject phoneJsonObj) {
        if (phoneJsonObj == null) {
            return null;
        }

        if (!Module.PHONE.equalsIgnoreCase(phoneJsonObj.optString(PhoneJSONConstant.MODULE))) {
            throw new ModuleException("module error! current module is " + Module.PHONE);
        }

        PhoneJson phoneJson = new PhoneJson();
        phoneJson.setSubVersion(phoneJsonObj.optString(PhoneJSONConstant.SUB_VERSION));
        phoneJson.setModule(phoneJsonObj.optString(PhoneJSONConstant.MODULE));
        phoneJson.setCardSlot(phoneJsonObj.optInt(PhoneJSONConstant.CARD_SLOT));
        phoneJson.setCallType(phoneJsonObj.optInt(PhoneJSONConstant.CALL_TYPE));
        phoneJson.setConnect(phoneJsonObj.optString(PhoneJSONConstant.CONNECT));
        phoneJson.setStatus(phoneJsonObj.optString(PhoneJSONConstant.STATUS));
        phoneJson.setName(phoneJsonObj.optString(PhoneJSONConstant.NAME));
        phoneJson.setNumber(phoneJsonObj.optString(PhoneJSONConstant.NUMBER));
        phoneJson.setSerialno(phoneJsonObj.optString(PhoneJSONConstant.SERIAL_NO));
        return phoneJson;
    }
}
