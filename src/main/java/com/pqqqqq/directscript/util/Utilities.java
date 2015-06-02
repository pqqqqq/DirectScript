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
}
