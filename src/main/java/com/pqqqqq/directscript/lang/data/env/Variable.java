package com.pqqqqq.directscript.lang.data.env;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a memory section that contains a {@link Literal} and is read by a specific name
 */
public class Variable extends LiteralHolder {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]([A-Za-z0-9]|\\.)*$");

    private final String name;
    private final boolean isFinal;

    /**
     * Creates a new variable with the corresponding name that has a value of {@link Literal#empty()} and is not final
     *
     * @param name the name
     */
    public Variable(String name) {
        super();
        this.name = name;
        this.isFinal = false;
    }

    /**
     * Creates a new variable with the corresponding name and {@link Literal} data, and is not final
     *
     * @param name the name
     * @param data the data
     */
    public Variable(String name, Literal data) {
        super(data);
        this.name = name;
        this.isFinal = false;
    }

    /**
     * Creates a new variable with the corresponding name, {@link Literal} data and finality
     *
     * @param name    the name
     * @param data    the data
     * @param isFinal whether this variable is final
     */
    public Variable(String name, Literal data, boolean isFinal) {
        super(data);
        this.name = name;
        this.isFinal = isFinal;
    }

    /**
     * Creates new empty variable (with null name). This is analogous to <code>new Variable(null)</code>
     *
     * @return an empty variable
     */
    public static Variable empty() {
        return new Variable(null);
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
     * Gets the name of this variable
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public void setData(Literal data) {
        checkState(!isFinal, "You cannot change the value of a finalized vaiable");
        forceSetData(data);
    }

    private void forceSetData(Literal data) {
        checkNotNull(data, "Data itself cannot be null. Use Literal#empty for null data");
        super.setData(data);
    }

    /**
     * Gets whether this variable's {@link Literal} data is final, or cannot be changed
     * @return true if constant
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Copies this variable into a new instance
     * @return the copied variable
     */
    public Variable copy() {
        return new Variable(name, super.copy().getData(), isFinal);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("data", getData())
                .add("isFinal", isFinal).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, getData(), isFinal);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Variable) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }
}
