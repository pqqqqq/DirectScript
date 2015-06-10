package com.pqqqqq.directscript.lang.util;

import org.spongepowered.api.text.Texts;

import java.io.File;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Utilities {

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

    public static String fullLineTrim(String line) {
        line = line.trim(); // Actual trim first

        if (line.contains("  ")) { // Get rid of double spaces
            line = line.replace("  ", " ");
            return fullLineTrim(line);
        }

        return line;
    }

    public static <T> void buildToIndex(List<T> list, int index, ICopyable<T> defaultValue) {
        checkState(index >= 0, "Index must be positive");

        if (index < list.size()) {
            return; // No need for this
        }

        for (int i = list.size(); i <= index; i++) { // Go from the current size to the index
            list.add(defaultValue.copy()); // Add default value
        }
    }

    @SuppressWarnings("deprecation")
    public static String formatColour(String str) {
        if (str == null) {
            return null;
        }

        return str.replaceAll("&([0-9a-fA-FkKlLmMnNoOrR])", Texts.getLegacyChar() + "$1");
    }

    @SuppressWarnings("deprecation")
    public static String unformatColour(String str) {
        if (str == null) {
            return null;
        }

        return str.replaceAll(Texts.getLegacyChar() + "([0-9a-fA-FkKlLmMnNoOrR])", "&$1");
    }
}
