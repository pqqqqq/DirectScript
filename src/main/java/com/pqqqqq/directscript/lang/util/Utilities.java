package com.pqqqqq.directscript.lang.util;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.DirectScript;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A generic static utility class
 */
public class Utilities {
    static final Random random = new Random();
    static final Pattern timePattern = Pattern.compile("(\\d+?)(\\D+|$)");
    static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))"; // Credits to http://stackoverflow.com/questions/2206378/how-to-split-a-string-but-also-keep-the-delimiters

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
    public static <T> boolean buildToIndex(List<T> list, int index, T defaultValue) {
        checkState(index >= 0, "Index must be positive");

        if (index < list.size()) {
            return false; // No need for this
        }

        for (int i = list.size(); i <= index; i++) { // Go from the current size to the index
            list.add(defaultValue); // Add default value
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    /**
     * Formats the colour into legacy minecraft format
     */
    public static String formatColour(String str) {
        if (str == null) {
            return null;
        }

        return TextSerializers.formattingCode('&').replaceCodes(str, TextSerializers.LEGACY_FORMATTING_CODE);

        //return str.replaceAll("&([0-9a-fA-FkKlLmMnNoOrR])", "§$1");
    }

    @SuppressWarnings("deprecation")
    public static Text getText(String str) {
        if (str == null) {
            return null;
        }

        return TextSerializers.LEGACY_FORMATTING_CODE.deserialize(str);
    }

    @SuppressWarnings("deprecation")
    /**
     * Unformats the colour into easy & colour codes
     */
    public static String unformatColour(String str) {
        if (str == null) {
            return null;
        }

        return str.replaceAll("§([0-9a-fA-FkKlLmMnNoOrR])", "&$1");
    }

    /**
     * Removes all non-alphanumeric characters from a string
     * @param string the string
     * @return the string, only 1-z and 0-9
     */
    public static String removeNonAlphanumeric(String string) {
        return string.replaceAll("[^a-zA-Z0-9]", "");
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

        return Optional.empty();
    }

    /**
     * Temporary method to retrieve a {@link Key} with the given name
     * @param name the name of the key
     * @return the {@link Optional} key
     */
    public static Optional<Key> getKey(String name) {
        name = name.trim().replace("_", "").replace(" ", "");
        try {
            for (Field field : Keys.class.getFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    Object obj = field.get(null);

                    if (obj instanceof Key) {
                        if (name.equalsIgnoreCase(field.getName().trim().replace("_", "").replace(" ", ""))) {
                            return Optional.of((Key) obj);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    // STOLEN FROM BUKKIT
    /**
     * Gets a unit-vector pointing in the direction that this Location is
     * facing.
     *
     * @return a vector pointing the direction of this location
     */
    public static Vector3d getDirection(Vector3d rotation) {
        double x, y, z;
        double rotX = rotation.getY(); // This was reversed for sponge
        double rotY = rotation.getX();

        y = -Math.sin(Math.toRadians(rotY));
        double xz = Math.cos(Math.toRadians(rotY));

        x = -xz * Math.sin(Math.toRadians(rotX));
        z = xz * Math.cos(Math.toRadians(rotX));

        return new Vector3d(x, y, z);
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

    /**
     * A versatile method that retrieves an {@link Optional} {@link TimeUnit} from any of its aliases
     *
     * @param name the name
     * @return the time unit, or {@link Optional#empty()}
     */
    public static Optional<TimeUnit> getTimeUnit(String name) {
        switch (name.toLowerCase()) { // we want versatility here
            // Nano
            case "nanosecond":
            case "nanoseconds":
            case "nanos":
            case "ns":
                return Optional.of(TimeUnit.NANOSECONDS);

            // Micro
            case "microsecond":
            case "microseconds":
            case "micros":
            case "us":
                return Optional.of(TimeUnit.MICROSECONDS);

            // Millis
            case "millisecond":
            case "milliseconds":
            case "millis":
            case "ms":
                return Optional.of(TimeUnit.MILLISECONDS);

            // Seconds
            case "second":
            case "seconds":
            case "s":
                return Optional.of(TimeUnit.SECONDS);

            // Minutes
            case "minute":
            case "minutes":
            case "min":
            case "mins":
            case "m":
                return Optional.of(TimeUnit.MINUTES);

            // Hours
            case "hour":
            case "hours":
            case "h":
                return Optional.of(TimeUnit.HOURS);

            // Days
            case "day":
            case "days":
            case "d":
                return Optional.of(TimeUnit.DAYS);
        }

        return Optional.empty();
    }

    public static <K, V> V getMapType(Map<K, V> map, Predicate<K> predicate) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.test(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * <p>Parses a simple formatted string with variable {@link TimeUnit}s embedded.</p>
     * <p>Each term should consist of an integer, followed by the time unit in any of its respective aliases.</p>
     * <p>Example string: 14d5m19s99ms or 14d 5m 19s 99ms (spaces are permitted)</p>
     * <p>The output will ALWAYS be in {@link TimeUnit#MICROSECONDS}.</p>
     *
     * @param formattedString the formatted string
     * @return the time, in microseconds
     */
    public static long getFormattedTime(String formattedString) {
        long resultantTime = 0L;
        Matcher matcher = timePattern.matcher(formattedString);

        while (matcher.find()) {
            String term = matcher.group().trim(); // Spaces are permitted
            String[] numberSplit = term.split(String.format(WITH_DELIMITER, "\\D"), 2); // Split once and include delimiters

            Double time = Double.parseDouble(numberSplit[0].trim());
            if (numberSplit.length == 1) {
                resultantTime += TimeUnit.MICROSECONDS.convert(time.longValue(), TimeUnit.SECONDS); // By default, assume we're given seconds
                continue;
            }

            TimeUnit timeUnit = getTimeUnit(numberSplit[1].trim()).get();
            resultantTime += TimeUnit.MICROSECONDS.convert(time.longValue(), timeUnit);
        }

        return resultantTime;
    }

    /**
     * Creates a new {@link HashMap} with a key and value array that are standardized by their indices
     *
     * @param keys   the key array
     * @param values the value array
     * @param <K>    the key type
     * @param <V>    the value type
     * @return the new hash map
     */
    public static <K, V> Map<K, V> newHashMap(K[] keys, V[] values) {
        checkState(checkNotNull(keys, "Keys").length == checkNotNull(values, "Values").length, "Arrays must be the same size");
        Map<K, V> map = new HashMap<>();

        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }

    /**
     * Creates a new {@link HashMap} with a key and value array that are standarized by their indices, and {@link Function}s to convert the indices
     *
     * @param keyArray      the initial key array
     * @param valueArray    the initial value array
     * @param keyFunction   the function for key conversion
     * @param valueFunction the function for value conversion
     * @param <K>           the type for the key in the map
     * @param <V>           the type for the value in the map
     * @param <M>           the initial type for the key array
     * @param <N>           the initial type for the value array
     * @return the new hash map
     */
    public static <K, V, M, N> Map<K, V> newHashMap(M[] keyArray, N[] valueArray, Function<M, ? extends K> keyFunction, Function<N, ? extends V> valueFunction) {
        checkState(checkNotNull(keyArray, "Keys").length == checkNotNull(valueArray, "Values").length, "Arrays must be the same size");
        Map<K, V> map = new HashMap<>();

        for (int i = 0; i < keyArray.length; i++) {
            map.put(keyFunction.apply(keyArray[i]), valueFunction.apply(valueArray[i]));
        }

        return map;
    }
}
