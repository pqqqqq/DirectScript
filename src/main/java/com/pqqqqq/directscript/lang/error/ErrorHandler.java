package com.pqqqqq.directscript.lang.error;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kevin on 2015-06-02.
 * The error handler class that writes to a {@link PrintStream} about {@link Throwable} errors
 */
public class ErrorHandler {
    private static final File ERROR_FILE = new File("scripts/errors.log");
    private PrintStream writer;

    private ErrorHandler() {
        try {
            writer = new PrintStream(ERROR_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a new error handler instance
     *
     * @return a new instance
     */
    public static ErrorHandler instance() {
        return new ErrorHandler();
    }

    /**
     * Gets the error {@link File} that the {@link PrintStream} is writing to
     *
     * @return the error file
     */
    public static File getErrorFile() {
        return ERROR_FILE;
    }

    /**
     * Logs a given message with the {@link #timestamp()} included
     *
     * @param message the message to log
     */
    public void log(Object message) {
        writer.println(timestamp() + ": " + message.toString());
    }

    /**
     * Logs the {@link Throwable} into the {@link PrintStream}
     *
     * @param e the error
     */
    public void log(Throwable e) {
        e.printStackTrace(writer);
    }

    /**
     * Gets the timestamp, formatted by {@link SimpleDateFormat#getDateTimeInstance()}
     *
     * @return the timestamp
     */
    public String timestamp() {
        return "[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "]";
    }

    /**
     * Flushes the {@link PrintStream}
     */
    public void flush() {
        writer.flush();
    }

    /**
     * Closes the {@link PrintStream}
     */
    public void close() {
        writer.close();
    }
}
