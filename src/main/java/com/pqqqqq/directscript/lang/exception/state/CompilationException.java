package com.pqqqqq.directscript.lang.exception.state;

import com.pqqqqq.directscript.lang.exception.DirectScriptLanguageException;

/**
 * Created by Kevin on 2015-12-15.
 * A directscript exception that occurred during compilation
 */
public class CompilationException extends DirectScriptLanguageException {

    /**
     * Creates an empty compilation exception
     */
    public CompilationException() {
        super();
    }

    /**
     * Creates a compilation exception with the given message
     *
     * @param message the message
     */
    public CompilationException(String message) {
        super(message);
    }

    /**
     * Creates a compilation exception using {@link String#format(String, Object...)}
     *
     * @param message the message
     * @param args    the formatting arguments
     */
    public CompilationException(String message, Object... args) {
        super(message, args);
    }

    /**
     * Creates a compilation exception using {@link String#format(String, Object...)}
     *
     * @param cause   the cause
     * @param message the message
     * @param args    the formatting arguments
     */
    public CompilationException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
