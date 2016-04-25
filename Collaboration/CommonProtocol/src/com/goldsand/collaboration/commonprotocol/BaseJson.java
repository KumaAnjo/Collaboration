package com.goldsand.collaboration.commonprotocol;

public abstract class BaseJson {
    public String Module = "unknow";

    public String getModule() {
        return Module;
    }

    public void setModule(String module) {
        this.Module = module;
    }
}
