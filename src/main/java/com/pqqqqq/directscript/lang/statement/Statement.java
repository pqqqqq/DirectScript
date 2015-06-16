package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

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
     * An enumeration of execution times, to be used by {@link Line} {@link com.google.common.base.Predicate}s when executing a {@link com.pqqqqq.directscript.lang.script.ScriptInstance}
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

    /**
     * Denotes a class that represents a concept {@link Statement} that should work given the API, but is not yet implemented. These statements will be skipped by: {@link Statements#getStatement(Line)}
     */
    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public @interface Concept {
    }

    /**
     * Created by Kevin on 2015-06-12.
     * Represents an immutable argument for a {@link Statement} that has different properties
     */
    public static class Argument {
        private final String name;
        private final boolean parse;
        private final boolean optional;
        private final boolean modifier;
        private final boolean rest;

        Argument(String name, boolean parse, boolean optional, boolean modifier, boolean rest) {
            this.name = name;
            this.parse = parse;
            this.optional = optional;
            this.modifier = modifier;
            this.rest = rest;
        }

        /**
         * Gets the {@link Argument} {@link Builder}
         *
         * @return the builder
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Gets the name of this argument
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets if this argument should be parsed by {@link com.pqqqqq.directscript.lang.data.Sequencer#parse(String)}
         *
         * @return whether to parse this argument
         */
        public boolean doParse() {
            return parse;
        }

        /**
         * Gets if this argument is optional
         *
         * @return true if optional
         */
        public boolean isOptional() {
            return optional;
        }

        /**
         * Gets if this argument is a modifier, and therefore its value must equal {@link #getName()}
         *
         * @return true if a modifier
         */
        public boolean isModifier() {
            return modifier;
        }

        /**
         * Gets if this argument uses the rest of the line as its argument
         * @return true if it uses the rest of the line
         */
        public boolean isRest() {
            return rest;
        }

        /**
         * The builder for {@link Argument}s
         */
        public static class Builder {
            private String name = null;
            private boolean parse = true;
            private boolean optional = false;
            private boolean modifier = false;
            private boolean rest = false;

            Builder() { // Default view
            }

            /**
             * Sets the name of the argument
             *
             * @param name the new name
             * @return this builder, for fluency
             * @see Argument#getName()
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Sets the parse value of the argument
             *
             * @param parse the new parse value
             * @return this builder, for fluency
             * @see Argument#doParse()
             */
            public Builder parse(boolean parse) {
                this.parse = parse;
                return this;
            }

            /**
             * Toggles the parse value of the argument
             *
             * @return this builder, for fluency
             * @see Argument#doParse()
             */
            public Builder parse() {
                return parse(!parse);
            }

            /**
             * Sets the optional value of the argument
             *
             * @param optional the new optional value
             * @return this builder, for fluency
             * @see Argument#isOptional()
             */
            public Builder optional(boolean optional) {
                this.optional = optional;
                return this;
            }

            /**
             * Toggles the optional value of the argument
             *
             * @return this builder, for fluency
             * @see Argument#isOptional()
             */
            public Builder optional() {
                return optional(!optional);
            }

            /**
             * Sets the modifier value of the argument
             *
             * @param modifier the new modifier value
             * @return this builder, for fluency
             * @see Argument#isModifier()
             */
            public Builder modifier(boolean modifier) {
                this.modifier = modifier;
                return this;
            }

            /**
             * Toggles the modifier value of the argument
             *
             * @return this builder, for fluency
             * @see Argument#isModifier()
             */
            public Builder modifier() {
                return modifier(!modifier);
            }

            /**
             * Sets the rest value of the argument
             * @param rest the new rest value
             * @return this builder, for fluency
             * @see Argument#isRest()
             */
            public Builder rest(boolean rest) {
                this.rest = rest;
                return this;
            }

            /**
             * Toggles the rest value of the argument
             *
             * @return this builder, for fluency
             * @see Argument#isRest()
             */
            public Builder rest() {
                return rest(!rest);
            }

            /**
             * Builds the current builder data into a {@link Argument}
             *
             * @return the new argument instance
             */
            public Argument build() {
                checkNotNull(name, "Name cannot be null.");
                return new Argument(name, parse, optional, modifier, rest);
            }
        }
    }

    /**
     * Created by Kevin on 2015-06-02.
     * Represents the immutable result of executing a {@link Context} by {@link Context#run()}
     */
    public static class Result<T> {
        private static final Result<Object> SUCCESS = builder().success().build();
        private static final Result<Object> FAILURE = builder().failure().build();

        private final Optional<T> result;
        private final Optional<Literal<T>> literalResult;
        private final boolean success;

        Result(T result, Literal<T> literalResult, boolean success) {
            this.result = Optional.fromNullable(result);
            this.literalResult = Optional.fromNullable(literalResult);
            this.success = success;
        }

        /**
         * Gets a new {@link Builder} instance
         *
         * @param <T> the type for the builder/result
         * @return the new builder instance
         */
        public static <T> Builder<T> builder() {
            return new Builder<T>();
        }

        /**
         * Gets a success result that is cast to a generic type
         *
         * @param <T> the generic type for this result
         * @return the result
         */
        public static <T> Result<T> success() {
            return (Result<T>) SUCCESS;
        }

        /**
         * Gets a failure result that is cast to a generic type
         *
         * @param <T> the generic type for this result
         * @return the result
         */
        public static <T> Result<T> failure() {
            return (Result<T>) FAILURE;
        }

        /**
         * Gets the {@link Optional} result of this {@link Result}
         *
         * @return the result
         */
        public Optional<T> getResult() {
            return result;
        }

        /**
         * Gets the {@link Optional} {@link Literal} result of this {@link Result}
         *
         * @return the literal result
         */
        public Optional<Literal<T>> getLiteralResult() {
            return literalResult;
        }

        /**
         * Gets if this {@link Result} represents a successul result
         *
         * @return true if successful
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * The builder for building {@link Result}s
         *
         * @param <T> the type to cast the result to
         */
        public static class Builder<T> {
            private T result = null;
            private Literal<T> literalResult = null;
            private Boolean success = null;

            Builder() {
            }

            /**
             * Sets the result of this {@link Result}
             *
             * @param result the new result
             * @return this builder, for fluency
             * @see Result#getResult()
             */
            public Builder<T> result(T result) {
                this.result = result;
                return this;
            }

            /**
             * Sets the {@link Literal} result of this {@link Result}
             *
             * @param literalResult the new result, to be created into a literal
             * @return this builder, for fluency
             * @see Result#getLiteralResult()
             */
            public Builder<T> literal(T literalResult) {
                this.literalResult = Literal.getLiteralBlindly(literalResult);
                return this;
            }

            /**
             * Sets the {@link Literal} result of this {@link Result}
             *
             * @param literalResult the new literal result
             * @return this builder, for fluency
             * @see Result#getLiteralResult()
             */
            public Builder<T> literal(Literal<T> literalResult) {
                this.literalResult = literalResult;
                return this;
            }

            /**
             * Sets the success value of this {@link Result}
             *
             * @param success the success value
             * @return this builder, for fluency
             * @see Result#isSuccess()
             */
            public Builder<T> success(boolean success) {
                this.success = success;
                return this;
            }

            /**
             * Sets the success value of this {@link Result} to true
             *
             * @return this builder, for fluency
             * @see Result#isSuccess()
             */
            public Builder<T> success() {
                this.success = true;
                return this;
            }

            /**
             * Sets the success value of this {@link Result} to false
             *
             * @return this builder, for fluency
             * @see Result#isSuccess()
             */
            public Builder<T> failure() {
                this.success = false;
                return this;
            }

            /**
             * Builds the current data into a new {@link Result} instance
             *
             * @return the new result instance
             */
            public Result<T> build() {
                checkNotNull(success, "Success state must be specified");
                return new Result<T>(result, literalResult, success);
            }
        }
    }
}
