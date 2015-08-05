package com.pqqqqq.directscript.lang.util;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.Texts;

import java.io.File;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A generic static utility class
 */
public class Utilities {
    static Random random = new Random();

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

    /**
     * Temporary method to get the {@link Optional} {@link org.spongepowered.api.CatalogType} by its ID
     *
     * @param id the id
     * @return the catalog type
     */
    public static <T extends CatalogType> Optional<T> getType(Class<T> type, String id) {
        if (!id.contains(":")) {
            id = "minecraft:" + id;
        }

        for (T catalogType : DirectScript.instance().getGame().getRegistry().getAllOf(type)) {
            if (catalogType.getId().equalsIgnoreCase(id)) {
                return Optional.of(catalogType);
            }
        }

        return Optional.absent();
    }

    /**
     * Produces a random integer between the bounds
     *
     * @param min the minimum bound
     * @param max the maximum bound
     * @return the random integer
     */
    public static int randomInt(int min, int max) {
        checkState(max >= min, "Max must be larger than min");
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Produces a random double between the bounds
     *
     * @param min the minimum bound
     * @param max the maximum bound
     * @return the random integer
     */
    public static double randomDouble(double min, double max) {
        checkState(max >= min, "Max must be larger than min");
        return min + (max - min) * random.nextDouble();
    }
}
