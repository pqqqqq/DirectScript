package com.pqqqqq.directscript.lang.util;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-04.
 * A utility for string parsing
 */
public class StringParser {
    private static final StringParser INSTANCE = new StringParser();

    private StringParser() {
    }

    /**
     * Gets the string parser instance
     *
     * @return the instance
     */
    public static StringParser instance() {
        return INSTANCE;
    }

    /**
     * Parses a split of the specified string at the given delimiters, excluding quotes, round and square brackets
     *
     * @param string    the string to split
     * @param delimiter the delimiter strings
     * @return the split string array
     */
    public String[] parseSplit(String string, String... delimiter) {
        List<String> list = new ArrayList<String>();

        boolean quotes = false;
        int roundBrackets = 0, squareBrackets = 0, curlyBrackets = 0;
        String builder = "";

        for (int count = 0; count < string.length(); count++) {
            char c = string.charAt(count);

            if (c == '"') {
                if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                    quotes = !quotes;
                }
            } else if (!quotes) {
                if (c == '(') {
                    roundBrackets++;
                } else if (c == ')') {
                    roundBrackets--;
                } else if (c == '[') {
                    squareBrackets++;
                } else if (c == ']') {
                    squareBrackets--;
                } else if (c == '{') {
                    curlyBrackets++;
                } else if (c == '}') {
                    curlyBrackets--;
                }
            }

            builder += c;
            if (!quotes && roundBrackets == 0 && squareBrackets == 0 && curlyBrackets == 0) {
                for (String split : delimiter) {
                    if (builder.endsWith(split)) {
                        list.add(builder.substring(0, builder.length() - split.length()));
                        builder = "";
                        break;
                    }
                }
            }
        }

        if (!builder.isEmpty()) {
            list.add(builder);
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Parses the next {@link StringParser.SplitSequence} in a string by a prioritized split group
     *
     * @param string          the string to parse
     * @param delimiterGroups a two-dimensional delimiter string array, where each String[] in the String[][] is prioritized by its ordinal
     * @return the next split sequence, or null if none
     */
    public SplitSequence parseNextSequence(String string, String[]... delimiterGroups) {
        for (String[] splitGroup : delimiterGroups) {
            boolean quotes = false;
            int roundBrackets = 0, squareBrackets = 0, curlyBrackets = 0;
            String builder = "";
            SplitSequence current = null;

            for (int count = 0; count < string.length(); count++) {
                char c = string.charAt(count);

                if (c == '"') {
                    if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                        quotes = !quotes;
                    }
                } else if (!quotes) {
                    if (c == '(') {
                        roundBrackets++;
                    } else if (c == ')') {
                        roundBrackets--;
                    } else if (c == '[') {
                        squareBrackets++;
                    } else if (c == ']') {
                        squareBrackets--;
                    } else if (c == '{') {
                        curlyBrackets++;
                    } else if (c == '}') {
                        curlyBrackets--;
                    }
                }

                builder += c;
                if (!quotes && roundBrackets == 0 && squareBrackets == 0 && curlyBrackets == 0) {
                    for (String split : splitGroup) {
                        if (builder.endsWith(split)) {
                            String before = string.substring(0, count - split.length() + 1);
                            String after = string.substring(count + 1);

                            current = new SplitSequence(before, split, after);
                        }
                    }
                }
            }

            if (current != null) {
                return current;
            }
        }

        return null;
    }

    /**
     * Gets the first index of a find string, excluding quotes, round and square brackets
     *
     * @param string the string
     * @param find   the string to find
     * @return the index, or -1 if none are found
     */
    public int indexOf(String string, String find) {
        return indexOf(string, find, false);
    }

    /**
     * Gets the first index of a find string, excluding quotes, and optional exclusion of round and square brackets
     *
     * @param string        the string
     * @param find          the string to find
     * @param allowBrackets whether to allow the sequence inside of a bracket
     * @return the index, or -1 if none are found
     */
    public int indexOf(String string, String find, boolean allowBrackets) {
        boolean quotes = false;
        int roundBrackets = 0, squareBrackets = 0, curlyBrackets = 0;
        String builder = "";

        for (int count = 0; count < string.length(); count++) {
            char c = string.charAt(count);
            builder += c;

            if (!quotes && roundBrackets == 0 && squareBrackets == 0 && curlyBrackets == 0) {
                if (builder.endsWith(find)) {
                    return builder.length() - find.length();
                }
            }

            if (c == '"') {
                if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                    quotes = !quotes;
                }
            } else if (!quotes && !allowBrackets) {
                if (c == '(') {
                    roundBrackets++;
                } else if (c == ')') {
                    roundBrackets--;
                } else if (c == '[') {
                    squareBrackets++;
                } else if (c == ']') {
                    squareBrackets--;
                } else if (c == '{') {
                    curlyBrackets++;
                } else if (c == '}') {
                    curlyBrackets--;
                }
            }
        }

        return -1;
    }

    /**
     * Gets the last index of a find string, excluding quotes, round and square brackets
     *
     * @param string the string
     * @param find   the string to find
     * @return the index, or -1 if none are found
     */
    public int lastIndexOf(String string, String find) {
        return lastIndexOf(string, find, false);
    }

    /**
     * Gets the last index of a find string, excluding quotes, and optional exclusion of round and square brackets
     *
     * @param string        the string
     * @param find          the string to find
     * @param allowBrackets whether to allow the sequence inside of a bracket
     * @return the index, or -1 if none are found
     */
    public int lastIndexOf(String string, String find, boolean allowBrackets) {
        boolean quotes = false;
        int roundBrackets = 0, squareBrackets = 0, curlyBrackets = 0;
        int lastIndex = -1;
        String builder = "";

        for (int count = 0; count < string.length(); count++) {
            char c = string.charAt(count);
            builder += c;

            if (!quotes && roundBrackets == 0 && squareBrackets == 0 && curlyBrackets == 0) {
                if (builder.endsWith(find)) {
                    lastIndex = builder.length() - find.length();
                }
            }

            if (c == '"') {
                if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                    quotes = !quotes;
                }
            } else if (!quotes && !allowBrackets) {
                if (c == '(') {
                    roundBrackets++;
                } else if (c == ')') {
                    roundBrackets--;
                } else if (c == '[') {
                    squareBrackets++;
                } else if (c == ']') {
                    squareBrackets--;
                } else if (c == '{') {
                    curlyBrackets++;
                } else if (c == '}') {
                    curlyBrackets--;
                }
            }
        }

        return lastIndex;
    }

    /**
     * Trims a string with {@link String#trim()} and by removing any double whitespace
     *
     * @param string the string
     * @return the trimmed string
     */
    public String trim(String string) {
        string = string.trim(); // First trim using String#trim()
        boolean quotes = false;
        String builder = "";

        for (char c : string.toCharArray()) {
            if (c == '"') {
                if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                    quotes = !quotes;
                }
            }

            if (quotes || !Character.isWhitespace(c) || !builder.isEmpty() && !Character.isWhitespace(builder.charAt(builder.length() - 1))) { // trim() should get rid of leading whitespace, but just in case
                builder += c;
            }
        }

        return builder;
    }

    /**
     * Gets a {@link Pair} of Boolean, String that represents the block comment status and the current string
     *
     * @param blockComment whether there is an active block comment
     * @param string       the current string
     * @return the pair of boolean and string
     */
    public Pair<Boolean, String> removeComments(boolean blockComment, String string) {
        if (blockComment) {
            int endBlock = indexOf(string, "*/", true);

            if (endBlock >= 0) {
                return removeComments(false, string.substring(endBlock + 2));
            }
            return Pair.of(true, "");
        } else {
            int startBlock = indexOf(string, "/*", true);
            int inlineBlock = indexOf(string, "//", true);

            if (startBlock < 0 && inlineBlock < 0) {
                return Pair.of(false, string);
            }

            if (startBlock >= 0 && (inlineBlock < 0 || startBlock < inlineBlock)) {
                Pair<Boolean, String> booleanStringPair = removeComments(true, string.substring(startBlock + 2));
                return Pair.of(booleanStringPair.getLeft(), string.substring(0, startBlock) + booleanStringPair.getRight());
            }

            if (inlineBlock >= 0 && (startBlock < 0 || inlineBlock < startBlock)) {
                return Pair.of(false, string.substring(0, inlineBlock));
            }
        }

        return null;
    }

    /**
     * Represents an immutable split sequence, a {@link Triple} String of the before(L) and after(R) segments, as well as the delimiter(M)
     */
    public static class SplitSequence extends Triple<String, String, String> {
        private final String beforeSegment;
        private final String delimiter;
        private final String afterSegment;

        /**
         * Creates a new split sequence
         *
         * @param beforeSegment the before segment
         * @param delimiter     the delimiter
         * @param afterSegment  the after segment
         */
        public SplitSequence(String beforeSegment, String delimiter, String afterSegment) {
            this.beforeSegment = beforeSegment;
            this.delimiter = delimiter;
            this.afterSegment = afterSegment;
        }

        @Override
        public String getLeft() {
            return beforeSegment;
        }

        @Override
        public String getMiddle() {
            return delimiter;
        }

        @Override
        public String getRight() {
            return afterSegment;
        }

        /**
         * Gets the before segment. This is analogous to {@link #getLeft()}
         *
         * @return the before segment string
         */
        public String getBeforeSegment() {
            return beforeSegment;
        }

        /**
         * Gets the split. This is analogous to {@link #getMiddle()}
         *
         * @return the split
         */
        public String getDelimiter() {
            return delimiter;
        }

        /**
         * Gets the after segment. This is analogous to {@link #getRight()}
         *
         * @return the after segment string
         */
        public String getAfterSegment() {
            return afterSegment;
        }
    }
}
