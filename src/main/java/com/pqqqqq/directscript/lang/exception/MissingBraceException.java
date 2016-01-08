package com.pqqqqq.directscript.lang.exception;

/**
 * Created by Kevin on 2015-12-15.
 * Thrown when a line is missing its brace partner ({})
 */
public class MissingBraceException extends DirectScriptLanguageException {

    /**
     * Creates an empty missing brace exception
     */
    public MissingBraceException() {
        super();
    }

    /**
     * Creates a missing brace exception with the given message
     *
     * @param message the message
     */
    public MissingBraceException(String message) {
        super(message);
    }

    /**
     * Creates a missing brace exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public MissingBraceException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a missing brace exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public MissingBraceException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
