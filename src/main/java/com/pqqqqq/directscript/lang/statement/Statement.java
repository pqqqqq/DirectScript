package com.pqqqqq.directscript.lang.statement;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;

import java.util.regex.Pattern;

/**
 * Created by Kevin on 2015-06-12.
 * The abstract implementation of statements
 */
public abstract class Statement<T> {
    private final String identifierPatternString;
    private final String prefixPatternString;
    private final String suffixPatternString;
    private final Pattern matchPattern;

    protected Statement() {
        this.identifierPatternString = genIdentifierPatternString();
        this.prefixPatternString = genPrefixPatternString();
        this.suffixPatternString = genSuffixPatternString();
        this.matchPattern = genMatchPattern();
    }

    // Public overridable methods
    public String getPrefix() {
        return "";
    }

    public String getSuffix() {
        return "";
    }

    public String getSplitString() {
        return ",";
    }

    public boolean doesUseBrackets() {
        return true;
    }

    public ExecutionTime getExecutionTime() {
        return ExecutionTime.RUNTIME;
    }

    // Public final non-overriable getter methods
    public final String getIdentifierPatternString() {
        return identifierPatternString;
    }

    public final String getPrefixPatternString() {
        return prefixPatternString;
    }

    public final String getSuffixPatternString() {
        return suffixPatternString;
    }

    public final Pattern getMatchPattern() {
        return matchPattern;
    }

    public final boolean matches(Line line) {
        return matchPattern.matcher(line.getLine()).matches();
    }

    // Abstract methods
    public abstract String[] getIdentifiers();

    public abstract Argument[] getArguments();

    public abstract Result<T> run(Context ctx);

    // Private generator methods
    private String genIdentifierPatternString() {
        String identifierString = "(";

        for (String identifier : getIdentifiers()) {
            identifierString += "\\Q" + identifier + "\\E|";
        }

        return identifierString.substring(0, identifierString.length() - 1) + ")";
    }

    private String genPrefixPatternString() {
        return getPrefix().isEmpty() ? "" : "\\Q" + getPrefix() + "\\E";
    }

    private String genSuffixPatternString() {
        return getSuffix().isEmpty() ? "" : "\\Q" + getSuffix() + "\\E";
    }

    private Pattern genMatchPattern() {
        if (!doesUseBrackets()) {
            return Pattern.compile("^(\\s*?)" + genPrefixPatternString() + genIdentifierPatternString() + "(.*?)" + genSuffixPatternString() + "(\\s*?)$");
        }
        return Pattern.compile("^(\\s*?)" + genPrefixPatternString() + genIdentifierPatternString() + "(\\s*?)\\((.*?)\\)(\\s*?)" + genSuffixPatternString() + "(\\s*?)$");
    }

    public enum ExecutionTime {
        RUNTIME, COMPILE, ALWAYS
    }
}
