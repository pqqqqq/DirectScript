package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-12.
 * The abstract implementation of statements
 */
public abstract class Statement<T> {
    private final Syntax syntax;

    protected Statement(Syntax syntax) {
        this.syntax = syntax;
    }

    /**
     * Gets the {@link Syntax} for this {@link Statement}
     * @return the syntax
     */
    public Syntax getSyntax() {
        return this.syntax;
    }

    /**
     * Runs this {@link Statement} by the given {@link Context}
     *
     * @param ctx the context
     * @return the {@link Result} of the execution
     */
    public abstract Result<T> run(Context ctx);

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    /**
     * An enumeration of execution times, to be used by {@link Line} {@link com.google.common.base.Predicate}s when executing a {@link com.pqqqqq.directscript.lang.script.ScriptInstance}
     *
     * @see Syntax#getExecutionTime()
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
     * Denotes a class that represents a concept {@link Statement} that should work given the Sponge API, but is not yet implemented. These statements will be skipped by: {@link Statements#getStatement(String)}
     */
    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public @interface Concept {
    }

    /**
     * An immutable class that denotes a {@link Statement}'s syntax
     */
    public static class Syntax {
        private final String[] identifiers;
        private final String prefix;
        private final String suffix;
        private final boolean doesUseBrackets;
        private final ExecutionTime executionTime;
        private final Arguments[] arguments;

        // Generated
        private final Pattern matchPattern;

        Syntax(String[] identifiers, String prefix, String suffix, boolean doesUseBrackets, ExecutionTime executionTime, Arguments[] arguments) {
            this.identifiers = identifiers;
            this.prefix = prefix;
            this.suffix = suffix;
            this.doesUseBrackets = doesUseBrackets;
            this.executionTime = executionTime;
            this.arguments = arguments;

            this.matchPattern = genMatchPattern();
        }

        /**
         * Gets a new {@link Syntax.Builder} instance
         *
         * @return the builder
         */
        public static Syntax.Builder builder() {
            return new Builder();
        }

        /**
         * Gets the identifiers for this statement, one of which is required to precede the prefix
         *
         * @return the identifier strings
         */
        public String[] getIdentifiers() {
            return identifiers;
        }

        /**
         * Gets the prefix for this statement. A {@link Line} must start with this to be considered (excluding whitespace)
         *
         * @return the prefix
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Gets the suffix for this statement. A {@link Line} must end with this to be considered (excluding whitespace)
         *
         * @return the suffix
         */
        public String getSuffix() {
            return suffix;
        }

        /**
         * Gets if this statement only checks for arguments that are inside brackets '()'
         *
         * @return true if uses argument brackets
         */
        public boolean doesUseBrackets() {
            return doesUseBrackets;
        }

        /**
         * Gets the {@link com.pqqqqq.directscript.lang.statement.Statement.ExecutionTime} for this statement
         *
         * @return the execution time
         */
        public ExecutionTime getExecutionTime() {
            return executionTime;
        }

        /**
         * Gets the array of {@link Arguments} for this statement
         *
         * @return the argument syntaxes
         */
        public Arguments[] getArguments() {
            return arguments;
        }

        /**
         * Gets whether a given line is applicable to this statement. This is analogous to: <code>getMatchPattern().matcher(line).matches()</code>
         *
         * @param line
         * @return
         */
        public boolean matches(String line) {
            return matchPattern.matcher(line).matches();
        }

        private String genIdentifierPatternString() {
            if (getIdentifiers() == null || getIdentifiers().length == 0) {
                return "";
            }

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
         * <p>The {@link Statement.Syntax} builder class</p>
         * <p>Defaults:</p>
         * <ul>
         * <li>Identifiers: Empty
         * <li>Prefix: Empty
         * <li>Suffix: Empty
         * <li>Brackets: Yes
         * <li>Execution time: RUNTIME
         * <li>Argument syntaxes: Empty (no arguments)
         * </ul>
         */
        public static class Builder {
            private List<String> identifiers = new ArrayList<String>();
            private String prefix = "";
            private String suffix = "";
            private boolean doesUseBrackets = true;
            private ExecutionTime executionTime = ExecutionTime.RUNTIME;
            private List<Arguments> arguments = new ArrayList<Arguments>();

            Builder() { // Default visibility
            }

            /**
             * Adds the array of identifiers to this {@link Builder}
             *
             * @param identifiers the new identifiers
             * @return this builder, for fluency
             * @see Syntax#getIdentifiers()
             */
            public Builder identifiers(String... identifiers) {
                this.identifiers.addAll(Arrays.asList(identifiers));
                return this;
            }

            /**
             * Sets the prefix for this {@link Builder}
             *
             * @param prefix the new prefix
             * @return this builder, for fluency
             * @see Syntax#getPrefix()
             */
            public Builder prefix(String prefix) {
                this.prefix = prefix;
                return this;
            }

            /**
             * Sets the suffix for this {@link Builder}
             *
             * @param suffix the new suffix
             * @return this builder, for fluency
             * @see Syntax#getSuffix()
             */
            public Builder suffix(String suffix) {
                this.suffix = suffix;
                return this;
            }

            /**
             * Sets whether this {@link Builder} should use brackets
             *
             * @param doesUseBrackets the new brackets state
             * @return this builder, for fluency
             * @see Syntax#doesUseBrackets
             */
            public Builder brackets(boolean doesUseBrackets) {
                this.doesUseBrackets = doesUseBrackets;
                return this;
            }

            /**
             * Toggles whether this {@link Builder} should use brackets
             *
             * @return this builder, for fluency
             */
            public Builder brackets() {
                return brackets(!doesUseBrackets);
            }

            /**
             * Sets the {@link Statement.ExecutionTime} for this {@link Builder}
             *
             * @param executionTime the new execution time
             * @return this builder, for fluency
             * @see Syntax#getExecutionTime()
             */
            public Builder executionTime(ExecutionTime executionTime) {
                this.executionTime = executionTime;
                return this;
            }

            /**
             * Adds the array of {@link Arguments}es to this {@link Builder}
             *
             * @param arguments the new argument syntaxes
             * @return this builder, for fluency
             * @see Syntax#getArguments()
             */
            public Builder arguments(Arguments... arguments) {
                this.arguments.addAll(Arrays.asList(arguments));
                return this;
            }

            /**
             * Builds the {@link Syntax} instance
             *
             * @return the new syntax instance
             */
            public Syntax build() {
                if (this.arguments.isEmpty()) {
                    this.arguments.add(Arguments.empty());
                }

                Collections.sort(this.arguments);
                return new Syntax(identifiers.toArray(new String[identifiers.size()]), prefix, suffix, doesUseBrackets, executionTime, arguments.toArray(new Arguments[arguments.size()]));
            }
        }
    }

    /**
     * The {@link Argument} sequence for this {@link Statement}
     */
    public static class Arguments implements Comparable<Arguments> {
        private static final Arguments EMPTY = new Arguments(new Argument[0], new String[0]);
        private final Argument[] arguments;
        private final String[] delimiters;

        Arguments(Argument[] arguments, String[] delimiters) {
            this.arguments = arguments;
            this.delimiters = delimiters;
        }

        /**
         * Returns an empty {@link Arguments} (no {@link Statement.Argument}s)
         * @return the empty argument sequence
         */
        public static Arguments empty() {
            return EMPTY;
        }

        /**
         * <p>Creates a new {@link Arguments} instance with the given Object sequence</p>
         * <p>Accepted types: Argument and String</p>
         * @param sequence the sequence vararg
         * @return the new instance
         */
        public static Arguments of(Object... sequence) {
            checkNotNull(sequence, "Sequence cannot be null");

            Argument[] arguments = new Argument[(int) Math.ceil(sequence.length / 2D)];
            String[] delimiters = new String[(int) Math.floor(sequence.length / 2D)];

            Class<?> lastType = null;
            int argumentIndex = 0, delimiterIndex = 0;

            for (Object obj : sequence) {
                checkState(obj instanceof Argument || obj instanceof String, "Unknown type in argument syntax: " + obj.getClass().getName());
                checkState(lastType == null || !lastType.equals(obj.getClass()), "Do not repeat two types after each other");
                lastType = obj.getClass();

                if (obj instanceof Argument) {
                    arguments[argumentIndex++] = (Argument) obj;
                } else if (obj instanceof String) {
                    delimiters[delimiterIndex++] = (String) obj;
                }
            }
            return new Arguments(arguments, delimiters);
        }

        /**
         * Gets the {@link Statement.Argument} array sequence
         * @return the argument sequence
         */
        public Argument[] getArguments() {
            return arguments;
        }

        /**
         * Gets the string delimiters array sequence
         *
         * @return the delimiter sequence
         */
        public String[] getDelimiters() {
            return delimiters;
        }

        @Override
        public int compareTo(Arguments o) {
            if (arguments.length > o.getArguments().length || delimiters.length > o.getDelimiters().length) {
                return -1;
            }

            if (arguments.length < o.getArguments().length || delimiters.length < o.getDelimiters().length) {
                return 1;
            }

            return 0;
        }
    }

    /**
     * Created by Kevin on 2015-06-12.
     * Represents an immutable argument for a {@link Statement} that has different properties
     */
    public static class Argument {
        private final String name;
        private final boolean parse;

        Argument(String name, boolean parse) {
            this.name = name;
            this.parse = parse;
        }

        /**
         * <p>Creates a generic {@link Argument} with default options with the given name.</p>
         * <p>This method is analogous to: Statement.builder().name(String).build()</p>
         * @param name the name of the argument
         * @return the new argument instance
         */
        public static Argument from(String name) {
            return builder().name(name).build();
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
         * Gets if this argument should be parsed by {@link com.pqqqqq.directscript.lang.data.Sequencer#parse(Line, String)}
         *
         * @return whether to parse this argument
         */
        public boolean doParse() {
            return parse;
        }

        /**
         * The builder for {@link Argument}s
         */
        public static class Builder {
            private String name = null;
            private boolean parse = true;
            private boolean modifier = false;

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
             * Builds the current builder data into a {@link Argument}
             *
             * @return the new argument instance
             */
            public Argument build() {
                checkNotNull(name, "Name cannot be null.");
                return new Argument(name, parse);
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
                this.literalResult = Literal.fromObject(literalResult);
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
