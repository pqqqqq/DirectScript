package com.pqqqqq.directscript.util;

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
}
