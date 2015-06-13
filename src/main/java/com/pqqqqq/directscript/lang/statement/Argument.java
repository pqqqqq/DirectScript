package com.pqqqqq.directscript.lang.statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-12.
 * Represents an immutable argument for a {@link Statement} that has different properties
 */
public class Argument {
    private final String name;
    private final boolean parse;
    private final boolean optional;
    private final boolean modifier;

    Argument(String name, boolean parse, boolean optional, boolean modifier) {
        this.name = name;
        this.parse = parse;
        this.optional = optional;
        this.modifier = modifier;
    }

    /**
     * Gets the {@link Argument} {@link com.pqqqqq.directscript.lang.statement.Argument.Builder}
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
     * The builder for {@link Argument}s
     */
    public static class Builder {
        private String name = null;
        private boolean parse = true;
        private boolean optional = false;
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
         * @return this builder, for fluency
         * @see Argument#doParse()
         */
        public Builder parse() {
            return parse(!parse);
        }

        /**
         * Sets the optional value of the argument
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
         * Builds the current builder data into a {@link Argument}
         * @return the new argument instance
         */
        public Argument build() {
            checkNotNull(name, "Name cannot be null.");
            return new Argument(name, parse, optional, modifier);
        }
    }
}
