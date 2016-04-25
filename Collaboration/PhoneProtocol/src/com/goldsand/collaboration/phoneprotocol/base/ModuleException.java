package com.goldsand.collaboration.phoneprotocol.base;

public class ModuleException extends RuntimeException {

    private static final long serialVersionUID = 7812710183389028800L;

    /**
     * Constructs a new {@code ModuleException} that includes the current
     * stack trace.
     */
    public ModuleException() {
    }

    /**
     * Constructs a new {@code ModuleException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage
     *            the detail message for this exception.
     */
    public ModuleException(String detailMessage) {
        super(detailMessage);
    }
}
