package com.pqqqqq.directscript.lang.exception.handler;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kevin on 2015-06-02.
 * The error handler class that writes to a {@link PrintStream} about {@link Throwable} exceptions
 */
public class ExceptionHandler {
    private static final ExceptionHandler INSTANCE = new ExceptionHandler();
    private static final File ERROR_FILE = new File("scripts/errors.log");
    private PrintStream writer;

    private ExceptionHandler() {
    }

    /**
     * Gets the {@link ExceptionHandler} instance
     *
     * @return the instance
     */
    public static ExceptionHandler instance() {
        return INSTANCE;
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
     * Attaches this {@link ExceptionHandler}'s {@link PrintStream} to the error file, denoted by {@link #getErrorFile()}
     */
    public void attach() {
        try {
            writer = new PrintStream(ERROR_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        writer.print(timestamp() + ": ");
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
