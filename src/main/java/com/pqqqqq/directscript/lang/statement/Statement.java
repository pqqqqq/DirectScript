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

    /**
     * Gets the prefix for this statement. A {@link Line} must start with this to be considered (excluding whitespace)
     *
     * @return the prefix
     */
    public String getPrefix() {
        return "";
    }

    /**
     * Gets the suffix for this statement. A {@link Line} must end with this to be considered (excluding whitespace)
     *
     * @return the suffix
     */
    public String getSuffix() {
        return "";
    }

    /**
     * Gets the split string for this statement; where arguments are divided
     *
     * @return the split string
     */
    public String getSplitString() {
        return ",";
    }

    /**
     * Gets if this statement only checks for arguments that are inside brackets '()'
     *
     * @return true if uses argument brackets
     */
    public boolean doesUseBrackets() {
        return true;
    }

    /**
     * Gets the {@link com.pqqqqq.directscript.lang.statement.Statement.ExecutionTime} for this statement
     *
     * @return the execution time
     */
    public ExecutionTime getExecutionTime() {
        return ExecutionTime.RUNTIME;
    }

    // Public final non-overriable getter methods

    /**
     * Gets the identifier pattern string for {@link #getMatchPattern()}. This value is determined on initialization and never changes
     *
     * @return the identifier pattern string
     */
    public final String getIdentifierPatternString() {
        return identifierPatternString;
    }

    /**
     * Gets the prefix pattern string for {@link #getMatchPattern()}. This value is determined on initialization and never changes
     * @return the prefix pattern string
     */
    public final String getPrefixPatternString() {
        return prefixPatternString;
    }

    /**
     * Gets the suffix pattern string for {@link #getMatchPattern()}. This value is determined on initialization and never changes
     * @return the suffix pattern string
     */
    public final String getSuffixPatternString() {
        return suffixPatternString;
    }

    /**
     * Gets the {@link Pattern} that a {@link Line} must match to be heralded as this {@link Statement}. This value is determined on initialization and never changes
     * @return the pattern
     */
    public final Pattern getMatchPattern() {
        return matchPattern;
    }

    /**
     * Gets whether a given {@link Line} is applicable to this statement. This is analogous to: <code>getMatchPattern().matcher(line.getLine()).matches()</code>
     * @param line
     * @return
     */
    public final boolean matches(Line line) {
        return matchPattern.matcher(line.getLine()).matches();
    }

    // Abstract methods

    /**
     * Gets the identifiers for this statement, one of which is required to precede the prefix
     * @return the identifier strings
     */
    public abstract String[] getIdentifiers();

    /**
     * Gets the {@link Argument} array for this statement
     * @return the arguments
     */
    public abstract Argument[] getArguments();

    /**
     * Runs this {@link Statement} by the given {@link Context}
     * @param ctx the context
     * @return the {@link Result} of the execution
     */
    public abstract Result<T> run(Context ctx);

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

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

    /**
     * An enumeration of execution times, to be used by {@link Line} {@link com.google.common.base.Predicate}s when executing a {@link com.pqqqqq.directscript.lang.container.ScriptInstance}
     * @see Statement#getExecutionTime()
     */
    public enum ExecutionTime {
        /**
         * Represents that this {@link Statement} is to only be run at runtime
         */
        RUNTIME,

        /**
         * Represents that this {@link Statement} is to only be run when compiling
         */
        COMPILE,

        /**
         * Represents that this {@link Statement} is always to be run
         */
        ALWAYS
    }
}
