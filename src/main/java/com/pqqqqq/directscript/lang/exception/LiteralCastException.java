package com.pqqqqq.directscript.lang.exception;

import com.pqqqqq.directscript.lang.data.Literal;

/**
 * Created by Kevin on 2016-01-06.
 * Thrown when a literal cannot be cast properly, via {@link Literal#getAs(Class)}
 */
public class LiteralCastException extends DirectScriptLanguageException {

    /**
     * Creates an empty literal cast exception
     */
    public LiteralCastException() {
        super();
    }

    /**
     * Creates a literal cast exception with the given message
     *
     * @param message the message
     */
    public LiteralCastException(String message) {
        super(message);
    }

    /**
     * Creates a literal cast exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public LiteralCastException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a literal cast exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public LiteralCastException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
