package com.goldsand.collaboration.commonprotocol;

public class ModuleMismatchException extends RuntimeException {

    private static final long serialVersionUID = 7812710183389028792L;

    /**
     * Constructs a new {@code ModuleMismatchException} that includes the current
     * stack trace.
     */
    public ModuleMismatchException() {
    }

    /**
     * Constructs a new {@code ModuleMismatchException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage
     *            the detail message for this exception.
     */
    public ModuleMismatchException(String detailMessage) {
        super(detailMessage);
    }
}
