package com.goldsand.collaboration.phoneprotocol.base;

import com.goldsand.collaboration.commonprotocol.BaseJson;

public class PhoneJson extends BaseJson {
    private String SubVersion = "1.0";
    private String Serailno;
    private int CardSlot = 1;
    /* {@link Call.java} */
    private int CallType;
    private String Connect = "";
    private String Status = "idle";
    private String Name = "";
    private String Number = "";

    public PhoneJson() {
        setModule(com.goldsand.collaboration.phoneprotocol.Module.PHONE);
    }

    public String getSubVersion() {
        return SubVersion;
    }

    public void setSubVersion(String version) {
        this.SubVersion = version;
    }

    public String getSerialno() {
        return Serailno;
    }

    public void setSerialno(String number) {
        this.Serailno = number;
    }

    public int getCardSlot() {
        return CardSlot;
    }

    public void setCardSlot(int slot) {
        this.CardSlot = slot;
    }

    public int getCallType() {
        return this.CallType;
    }

    public void setCallType(int type) {
        this.CallType = type;
    }

    public String getConnect() {
        return Connect;
    }

    public void setConnect(String connect) {
        this.Connect = connect;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String state) {
        this.Status = state;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        this.Number = number;
    }

    public PhoneJson copy(PhoneJson json) {
        this.SubVersion = json.SubVersion;
        this.Serailno = json.Serailno;
        this.CardSlot = json.CardSlot;
        this.CallType = json.CallType;
        this.Connect = json.Connect;
        this.Status = json.Status;
        this.Name = json.Name;
        this.Number = json.Number;

        return this;
    }

    public PhoneJson clone() {
        PhoneJson json = new PhoneJson();
        json.SubVersion = this.SubVersion;
        json.Serailno = this.Serailno;
        json.CardSlot = this.CardSlot;
        json.CallType = this.CallType;
        json.Connect = this.Connect;
        json.Status = this.Status;
        json.Name = this.Name;
        json.Number = this.Number;

        return json;
    }
}
