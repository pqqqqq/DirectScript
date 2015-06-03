package com.pqqqqq.directscript.util;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Utilities {

    public static String fullLineTrim(String line) {
        line = line.trim(); // Actual trim first

        if (line.contains("  ")) { // Get rid of double spaces
            line = line.replace("  ", " ");
            return fullLineTrim(line);
        }

        return line;
    }

    public static Integer getInteger(String literal) {
        try {
            return Integer.parseInt(literal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Long getLong(String literal) {
        try {
            return Long.parseLong(literal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Float getFloat(String literal) {
        try {
            return Float.parseFloat(literal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double getDouble(String literal) {
        try {
            return Double.parseDouble(literal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getFileDiff(File root, File file) {
        return getFileDiff("", root, file);
    }

    private static String getFileDiff(String buffer, File root, File file) {
        if (file.equals(root)) {
            return buffer;
        }

        buffer = (buffer.isEmpty() ? file.getName() : file.getName() + "/" + buffer);
        return getFileDiff(buffer, root, file.getParentFile());
    }

    public static String unescape(String str) {
        return StringEscapeUtils.unescapeJava(str);
    }
}
