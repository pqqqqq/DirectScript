package com.pqqqqq.directscript.lang.exception;

/**
 * Created by Kevin on 2015-12-13.
 * A main {@link RuntimeException} that can be traced back to DirectScript's inherent language system
 */
public class DirectScriptLanguageException extends RuntimeException {

    /**
     * Creates an empty directscript exception
     */
    public DirectScriptLanguageException() {
        super();
    }

    /**
     * Creates a directscript exception with the given message
     *
     * @param message the message
     */
    public DirectScriptLanguageException(String message) {
        super(message);
    }

    /**
     * Creates a directscript exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public DirectScriptLanguageException(String message, Object... args) {
        super(String.format(message, args));
    }

    /**
     * Creates a directscript exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public DirectScriptLanguageException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }
}
