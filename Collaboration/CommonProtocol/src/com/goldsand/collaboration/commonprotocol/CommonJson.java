package com.goldsand.collaboration.commonprotocol;

public class CommonJson {
    private String Version = "1.0";
    private String Module = "unknow";
    private BaseJson ModuleObj;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        this.Version = version;
    }

    public String getModule(){
        return Module;
    }

    public void setModule(String module) {
        this.Module = module;
    }

    public BaseJson getModuleObj() {
        return ModuleObj;
    }

    public void setModuleObj(BaseJson obj) {
        this.ModuleObj = obj;
    }
}
