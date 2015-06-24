package com.pqqqqq.directscript.lang.util;

import org.spongepowered.api.text.Texts;

import java.io.File;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A generic static utility class
 */
public class Utilities {

    /**
     * Gets a double from a string, or null if none
     *
     * @param literal the string
     * @return the double, or null
     */
    public static Double getDouble(String literal) {
        try {
            return Double.parseDouble(literal);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets the string representation of the difference between a root file and a containing file within it
     *
     * @param root the root file
     * @param file a containing file
     * @return a string path
     */
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

    /**
     * Builds a {@link List} to the necessary index by placing default values at null locations
     *
     * @param list         the list
     * @param index        the index necessary
     * @param defaultValue a copyable instance of the copyable value
     * @param <T>          the generic type for the list and default value
     */
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
    /**
     * Formats the colour into legacy minecraft format
     */
    public static String formatColour(String str) {
        if (str == null) {
            return null;
        }

        return str.replaceAll("&([0-9a-fA-FkKlLmMnNoOrR])", Texts.getLegacyChar() + "$1");
    }

    @SuppressWarnings("deprecation")
    /**
     * Unformats the colour into easy & colour codes
     */
    public static String unformatColour(String str) {
        if (str == null) {
            return null;
        }

        return str.replaceAll(Texts.getLegacyChar() + "([0-9a-fA-FkKlLmMnNoOrR])", "&$1");
    }
}
