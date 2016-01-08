package com.pqqqqq.directscript.lang.exception;

import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-12-15.
 * Thrown when a {@link Line}'s {@link Statement} cannot be identified during compilation
 */
public class UnknownLineException extends DirectScriptLanguageException {

    /**
     * Creates an empty unknown line exception
     */
    public UnknownLineException() {
        super();
    }

    /**
     * Creates a unknown line exception with the given message
     *
     * @param message the message
     */
    public UnknownLineException(String message) {
        super(message);
    }

    /**
     * Creates a unknown line exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public UnknownLineException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a unknown line exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public UnknownLineException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
