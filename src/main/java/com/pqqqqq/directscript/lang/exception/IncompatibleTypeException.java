package com.pqqqqq.directscript.lang.exception;

/**
 * Created by Kevin on 2015-12-13.
 * A {@link DirectScriptLanguageException} which signifies inconsistent/incompatible types
 */
public class IncompatibleTypeException extends DirectScriptLanguageException {

    /**
     * Creates an empty incompatible type exception
     */
    public IncompatibleTypeException() {
        super();
    }

    /**
     * Creates a incompatible type exception with the given message
     *
     * @param message the message
     */
    public IncompatibleTypeException(String message) {
        super(message);
    }

    /**
     * Creates a incompatible type exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public IncompatibleTypeException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a incompatible type exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public IncompatibleTypeException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
