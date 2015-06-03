package com.pqqqqq.directscript.lang.error;

import java.io.File;
import java.io.PrintStream;

/**
 * Created by Kevin on 2015-06-02.
 */
public class ErrorHandler {
    private static final File ERROR_FILE = new File("scripts/errors.log");
    private static final ErrorHandler INSTANCE = new ErrorHandler();
    private PrintStream writer;

    private ErrorHandler() {
        try {
            ERROR_FILE.createNewFile();
            writer = new PrintStream(ERROR_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ErrorHandler instance() {
        return INSTANCE;
    }

    public static File getErrorFile() {
        return ERROR_FILE;
    }

    public void log(Object message) {
        writer.println(message.toString());
    }

    public void log(Exception e) {
        e.printStackTrace(writer);
        writer.println();
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }
}
