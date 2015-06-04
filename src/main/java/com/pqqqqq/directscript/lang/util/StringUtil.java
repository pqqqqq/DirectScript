package com.pqqqqq.directscript.lang.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kevin on 2015-06-03.
 */
public class StringUtil {
    private static final String SUFFIX_QUOTE_SEQUENCE = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public static String fullLineTrim(String line) {
        line = line.trim(); // Actual trim first

        if (line.contains("  ")) { // Get rid of double spaces
            line = line.replace("  ", " ");
            return fullLineTrim(line);
        }

        return line;
    }

    public static String unescape(String str) {
        return StringEscapeUtils.unescapeJava(str);
    }

    public static String getSuffixQuoteSequenceFor(String... splits) {
        String splitStr = "(";
        for (String split : splits) {
            splitStr += "\\Q" + split + "\\E|"; // Quoted string here
        }

        return splitStr.substring(0, splitStr.length() - 1) + ")" + SUFFIX_QUOTE_SEQUENCE;
    }

    public static String[] splitNotInQuotes(String str, String... splits) {
        if (splits.length == 0) {
            throw new IllegalArgumentException("Must include at least one split string");
        }

        return str.split(getSuffixQuoteSequenceFor(splits), -1);
    }

    /**
     * Splits a {@link String} into a {@link List} of {@link Triple}s ordered as follows: (1) the before split (2) the sequence between splits (3) the after split
     * @param str the string
     * @param splits the possible split sequences
     * @return a list of pairs
     */
    public static List<Triple<String, String, String>> splitPairNoQuotes(String str, String... splits) {
        if (splits.length == 0) {
            throw new IllegalArgumentException("Must include at least one split string");
        }

        List<Triple<String, String, String>> list = new ArrayList<Triple<String, String, String>>();

        Pattern pattern = Pattern.compile(getSuffixQuoteSequenceFor(splits));
        Matcher matcher = pattern.matcher(str);

        int groups = matcher.groupCount();
        if (groups == 0) { // No matches here
            list.add(Triple.<String, String, String>of(null, str, null));
            return list;
        }

        int lastMatchIndex = 0;
        String lastSplitString = null;

        while (matcher.find()) {
            // Place the one before in the list
            String nextSplitString = matcher.group();
            list.add(Triple.of(lastSplitString, str.substring(lastMatchIndex, matcher.start()), nextSplitString));

            // Place the one after in cache to be next
            lastMatchIndex = matcher.end();
            lastSplitString = nextSplitString;
        }

        list.add(Triple.<String, String, String>of(lastSplitString, str.substring(lastMatchIndex), null)); // Add last in list
        return list;
    }
}
