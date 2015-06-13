package com.pqqqqq.directscript.lang.statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-12.
 */
public class Argument {
    private final String name;
    private final boolean parse;
    private final boolean optional;
    private final boolean matchName;

    Argument(String name, boolean parse, boolean optional, boolean matchName) {
        this.name = name;
        this.parse = parse;
        this.optional = optional;
        this.matchName = matchName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public boolean doParse() {
        return parse;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isMatchName() {
        return matchName;
    }

    public static class Builder {
        private String name = null;
        private boolean parse = true;
        private boolean optional = false;
        private boolean matchName = false;

        Builder() { // Default view
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parse(boolean parse) {
            this.parse = parse;
            return this;
        }

        public Builder parse() {
            return parse(!parse);
        }

        public Builder optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder optional() {
            return optional(!optional);
        }

        public Builder matchName(boolean matchName) {
            this.matchName = matchName;
            return this;
        }

        public Builder matchName() {
            return matchName(!matchName);
        }

        public Argument build() {
            checkNotNull(name, "Name cannot be null.");
            return new Argument(name, parse, optional, matchName);
        }
    }
}
