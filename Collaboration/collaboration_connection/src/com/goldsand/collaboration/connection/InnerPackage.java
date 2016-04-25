
package com.goldsand.collaboration.connection;

import java.io.Serializable;

public class InnerPackage implements Serializable {
    public static final String PASS = "pass";
    public static final String FAILED = "failed";

    private static final long serialVersionUID = 1L;
    private int value = 0x01;
    private String message = null;

    public InnerPackage(String message) {
        value++;
        this.message = message;
    }

    public InnerPackage() {
    }

    public String getMessage() {
        return message;
    }

    public static final boolean isHeatbeatPackage(InnerPackage pkg) {
        return pkg.value == 0x01;
    }

    public static final InnerPackage createAuthorizePackage(String message) {
        return new InnerPackage(message);
    }

    @Override
    public String toString() {
        return "value=" + value + " message=" + message;
    }
}
