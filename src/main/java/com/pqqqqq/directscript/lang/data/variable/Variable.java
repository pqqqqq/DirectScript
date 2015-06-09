package com.pqqqqq.directscript.lang.data.variable;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.util.ICopyable;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a memory section that contains a {@link Literal} and is read by a specific name
 */
public class Variable implements ICopyable<Variable> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9]*$");

    @Nonnull private final String name;
    @Nonnull
    private final boolean isFinal;
    @Nonnull private Literal data;

    public Variable(String name) {
        this(name, Literal.empty());
    }

    public Variable(String name, Literal data) {
        this(name, data, false);
    }

    public Variable(String name, Literal data, boolean isFinal) {
        this.name = name;
        forceSetData(data);
        this.isFinal = isFinal;
    }

    public static Variable empty() {
        return new Variable(null);
    }

    public static Pattern namePattern() {
        return NAME_PATTERN;
    }

    public String getName() {
        return name;
    }

    public Literal getData() {
        return data;
    }

    public void setData(Literal data) {
        checkState(!isFinal, "You cannot change the value of a finalized vaiable");
        forceSetData(data);
    }

    private void forceSetData(Literal data) {
        checkNotNull(data, "Data itself cannot be null. Use Literal#empty for null data");
        this.data = data;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public Variable copy() {
        return new Variable(name, data, isFinal);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("isFinal", isFinal)
                .add("data", data).toString();
    }
}
