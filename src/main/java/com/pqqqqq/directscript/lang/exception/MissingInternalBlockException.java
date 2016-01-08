package com.pqqqqq.directscript.lang.exception;

/**
 * Created by Kevin on 2015-12-15.
 * Thrown when a link is missing an internal block
 */
public class MissingInternalBlockException extends DirectScriptLanguageException {

    /**
     * Creates an empty missing internal block exception
     */
    public MissingInternalBlockException() {
        super();
    }

    /**
     * Creates a missing internal block exception with the given message
     *
     * @param message the message
     */
    public MissingInternalBlockException(String message) {
        super(message);
    }

    /**
     * Creates a missing internal block exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public MissingInternalBlockException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a missing internal block exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public MissingInternalBlockException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
