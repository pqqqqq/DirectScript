package com.pqqqqq.directscript.lang.exception;

import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-12-29.
 * An exception that is called when a line fails to execute
 *
 * @see Context#run()
 */
public class FailedExecutionException extends DirectScriptLanguageException {

    /**
     * Creates an empty failed execution exception
     */
    public FailedExecutionException() {
        super();
    }

    /**
     * Creates a failed execution exception with the given message
     *
     * @param message the message
     */
    public FailedExecutionException(String message) {
        super(message);
    }

    /**
     * Creates a failed execution exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public FailedExecutionException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a failed execution exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public FailedExecutionException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
