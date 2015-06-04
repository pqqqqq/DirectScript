package com.pqqqqq.directscript.lang.error;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kevin on 2015-06-02.
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

    public static ErrorHandler instance() {
        return new ErrorHandler();
    }

    public static File getErrorFile() {
        return ERROR_FILE;
    }

    public void log(Object message) {
        writer.println(timestamp() + ": " + message.toString());
    }

    public void log(Throwable e) {
        e.printStackTrace(writer);
    }

    public String timestamp() {
        return "[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "]";
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }
}
