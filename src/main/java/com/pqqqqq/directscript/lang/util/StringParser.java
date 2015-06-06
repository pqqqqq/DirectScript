package com.pqqqqq.directscript.lang.util;

import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-04.
 */
public class StringParser {
    private static final StringParser INSTANCE = new StringParser();

    private StringParser() {
    }

    public static StringParser instance() {
        return INSTANCE;
    }

    public String[] parseSplit(String string, String... splits) {
        List<SplitSequence> sequences = parseSplitSeq(string, splits);
        List<String> result = new ArrayList<String>();

        for (SplitSequence sequence : sequences) {
            result.add(sequence.getSequence());
        }

        return result.toArray(new String[result.size()]);
    }

    public List<SplitSequence> parseSplitSeq(String string, String... splits) {
        List<SplitSequence> list = new ArrayList<SplitSequence>();

        boolean quotes = false;
        int brackets = 0;
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
                    brackets++;
                } else if (c == ')') {
                    brackets--;
                }
            }

            builder += c;

            if (!quotes && brackets == 0) {
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

    public String getFirstBracket(String string) {
        boolean quotes = false;
        String builder = "";
        int brackets = -1;

        for (int count = 0; count < string.length(); count++) {
            char c = string.charAt(count);

            if (c == '"') {
                if (!builder.endsWith("\\") || builder.endsWith("\\\\")) {
                    quotes = !quotes;
                }
            } else if (!quotes) {
                if (c == '(') {
                    brackets = count;
                } else if (c == ')') {
                    if (brackets < 0) {
                        throw new IllegalArgumentException("Unknown stray right bracket");
                    }

                    return builder.substring(brackets, count) + ")";
                }
            }

            builder += c;
        }

        return null;
    }

    public static class SplitSequence extends Triple<String, String, String> {
        private final String beforeSplit;
        private final String sequence;
        private final String afterSplit;

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

        public String getBeforeSplit() {
            return beforeSplit;
        }

        public String getSequence() {
            return sequence;
        }

        public String getAfterSplit() {
            return afterSplit;
        }
    }
}
