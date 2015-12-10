package com.pqqqqq.directscript.lang.data.env;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.mutable.DataHolder;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a memory section that contains a {@link Literal} and is read by a specific name
 */
public class Variable extends DataHolder {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]([A-Za-z0-9]|\\.|\\_|\\:)*$");
    private static final Pattern ILLEGAL_NAMES = Pattern.compile("/^(local|global|public|final|parse|in|and|or)$/");

    private final String name;
    private final Environment environment;
    private final boolean isFinal;

    /**
     * Creates a new variable with the corresponding name that has a value of {@link Literal.Literals#EMPTY} and is not final
     *
     * @param name the name
     * @param environment the environment
     */
    public Variable(String name, Environment environment) {
        this(name, environment, Literal.Literals.EMPTY);
    }

    /**
     * Creates a new variable with the corresponding name and {@link Datum}, and is not final
     *
     * @param name the name
     * @param environment the environment
     * @param data the data
     */
    public Variable(String name, Environment environment, Datum data) {
        this(name, environment, data, false);
    }

    /**
     * Creates a new variable with the corresponding name, {@link Datum} and finality
     *
     * @param name    the name
     * @param data    the data
     * @param environment the environment
     * @param isFinal whether this variable is final
     */
    public Variable(String name, Environment environment, Datum data, boolean isFinal) {
        super(data);
        this.name = checkNotNull(name, "Name cannot be null.");
        this.environment = checkNotNull(environment, "Environment cannot be null.");
        this.isFinal = isFinal;
    }

    /**
     * Creates new empty variable (with null name). This is analogous to <code>new Variable(null)</code>
     *
     * @return an empty variable
     */
    public static Variable empty(Environment environment) {
        return new Variable(null, environment);
    }

    /**
     * Gets the name matching {@link Pattern} that all {@link Variable}s names must match to be created
     *
     * @return the name matching pattern
     */
    public static Pattern namePattern() {
        return NAME_PATTERN;
    }

    /**
     * Gets the {@link Pattern} for illegal {@link Variable} names
     * @return the pattern
     */
    public static Pattern illegalNames() {
        return ILLEGAL_NAMES;
    }

    /**
     * Gets the name of this variable
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the {@link Environment} of this variable
     *
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setDatum(Datum data) {
        checkState(!isFinal, "You cannot change the value of a finalized variable");
        forceSetData(data);
    }

    private void forceSetData(Datum data) {
        boolean unequal = !checkNotNull(data, "Data itself cannot be null. Use Literal#empty for null data").equals(getDatum());

        try {
            super.setDatum(data);
        } finally {
            if (unequal && !this.environment.suppressNotifications) {
                this.environment.notifyChange();
            }
        }
    }

    /**
     * Gets whether this variable's {@link Literal} data is final, or cannot be changed
     *
     * @return true if constant
     */
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("environment", environment)
                .add("data", getDatum())
                .add("isFinal", isFinal).toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode(); // We just want the natural object hashCode here, since Environments' HashMap requires a static hashCode
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Variable) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }
}
