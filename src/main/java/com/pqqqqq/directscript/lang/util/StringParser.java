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
     * Parses a split of the specified string at the given split points, excluding quotes, round and square brackets
     *
     * @param string the string to split
     * @param splits the split strings
     * @return the split string array
     */
    public String[] parseSplit(String string, String... splits) {
        List<SplitSequence> sequences = parseSplitSeq(string, splits);
        List<String> result = new ArrayList<String>();

        for (SplitSequence sequence : sequences) {
            result.add(sequence.getSequence());
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Parses a split of the specified string at the given split points into a {@link List} of {@link com.pqqqqq.directscript.lang.util.StringParser.SplitSequence}s, excluding quotes, round and square brackets
     *
     * @param string the string to split
     * @param splits the split strings
     * @return the list of split sequences
     */
    public List<SplitSequence> parseSplitSeq(String string, String... splits) {
        List<SplitSequence> list = new ArrayList<SplitSequence>();

        boolean quotes = false;
        int roundBrackets = 0, squareBrackets = 0;
        String builder = "";

        String lastSplitSeq = null;
        int lastSplitIndex = 0;

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
                }
            }

            builder += c;

            if (!quotes && roundBrackets == 0 && squareBrackets == 0) {
                for (String split : splits) {
                    if (builder.endsWith(split)) {
                        String sequence = string.substring(lastSplitIndex, builder.length() - split.length());
                        list.add(new SplitSequence(lastSplitSeq, sequence, split));

                        lastSplitSeq = split;
                        lastSplitIndex = count + 1;
                        break;
                    }
                }
            }
        }

        list.add(new SplitSequence(lastSplitSeq, string.substring(lastSplitIndex), null));
        return list;
    }

    /**
     * Gets the outer bracket of a string as specified by the starting and closing character, excluding quotes
     *
     * @param string    the string
     * @param startChar the opening character, eg '('
     * @param endChar   the closing character, eg ')'
     * @return the outer bracket (including the starting and ending characters)
     */
    public String getOuterBracket(String string, char startChar, char endChar) {
        boolean quotes = false;
        String builder = "";
        int brackets = -1, counter = 0;

        for (int count = 0; count < string.length(); count++) {
            char c = string.charAt(count);

            if (c == '"') {
                if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                    quotes = !quotes;
                }
            } else if (!quotes) {
                if (c == startChar) {
                    if (brackets < 0) {
                        brackets = count;
                    }
                    counter++;
                } else if (c == endChar) {
                    if (brackets < 0) {
                        throw new IllegalArgumentException("Unknown stray ending bracket");
                    }

                    counter--;
                    if (counter == 0) {
                        return builder.substring(brackets, count) + endChar;
                    }
                }
            }

            builder += c;
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
        boolean quotes = false;
        int roundBrackets = 0, squareBrackets = 0;
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
                }
            }

            builder += c;
            if (!quotes && roundBrackets == 0 && squareBrackets == 0) {
                if (builder.endsWith(find)) {
                    return builder.length() - find.length();
                }
            }
        }

        return -1;
    }

    /**
     * Gets the first index of a find string, excluding quotes, but allowing round and square brackets
     *
     * @param string the string
     * @param find   the string to find
     * @return the index, or -1 if none are found
     */
    public int indexOfAllowBrackets(String string, String find) {
        boolean quotes = false;
        String builder = "";

        for (int count = 0; count < string.length(); count++) {
            char c = string.charAt(count);

            if (c == '"') {
                if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                    quotes = !quotes;
                }
            }

            builder += c;
            if (!quotes) {
                if (builder.endsWith(find)) {
                    return builder.length() - find.length();
                }
            }
        }

        return -1;
    }

    /**
     * Gets a {@link Pair} of Boolean, String that represents the block comment status and the current string
     * @param blockComment whether there is an active block comment
     * @param string the current string
     * @return the pair of boolean and string
     */
    public Pair<Boolean, String> removeComments(boolean blockComment, String string) {
        if (blockComment) {
            int endBlock = indexOfAllowBrackets(string, "*/");

            if (endBlock >= 0) {
                return removeComments(false, string.substring(endBlock + 2));
            }
            return Pair.of(true, "");
        } else {
            int startBlock = indexOfAllowBrackets(string, "/*");
            int inlineBlock = indexOfAllowBrackets(string, "//");

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
     * Represents an immutable split sequence, a {@link Triple} String of the before(L) and after(R) split, as well as the sequence itself(M)
     */
    public static class SplitSequence extends Triple<String, String, String> {
        private final String beforeSplit;
        private final String sequence;
        private final String afterSplit;

        /**
         * Creates a new split sequence
         * @param beforeSplit the before split
         * @param sequence the actual sequence
         * @param afterSplit the after split
         */
        public SplitSequence(String beforeSplit, String sequence, String afterSplit) {
            this.beforeSplit = beforeSplit;
            this.sequence = sequence;
            this.afterSplit = afterSplit;
        }

        @Override
        public String getLeft() {
            return beforeSplit;
        }

        @Override
        public String getMiddle() {
            return sequence;
        }

        @Override
        public String getRight() {
            return afterSplit;
        }

        /**
         * Gets the before split. This is analogous to {@link #getLeft()}
         * @return the before split string
         */
        public String getBeforeSplit() {
            return beforeSplit;
        }

        /**
         * Gets the actual sequence. This is analogous to {@link #getMiddle()}
         * @return the actual sequence
         */
        public String getSequence() {
            return sequence;
        }

        /**
         * Gets the after split. This is analogous to {@link #getRight()}
         * @return the after split string
         */
        public String getAfterSplit() {
            return afterSplit;
        }
    }
}
