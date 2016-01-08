package com.pqqqqq.directscript.lang.exception.state;

import com.pqqqqq.directscript.lang.exception.DirectScriptLanguageException;

/**
 * Created by Kevin on 2015-12-15.
 * A directscript exception that occurred during execution
 */
public class ExecutionException extends DirectScriptLanguageException {

    /**
     * Creates an empty execution exception
     */
    public ExecutionException() {
        super();
    }

    /**
     * Creates a execution exception with the given message
     *
     * @param message the message
     */
    public ExecutionException(String message) {
        super(message);
    }

    /**
     * Creates a execution exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public ExecutionException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a execution exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public ExecutionException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
