package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.util.Utilities;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A literal is a value that is not dependent on any environment; a constant
 */
public class Literal<T> {
    private static final Literal EMPTY = new Literal();
    private final Optional<T> value;

    private Literal() {
        this(null);
    }

    private Literal(T value) {
        this.value = Optional.fromNullable(value);
    }

    public static Literal empty() {
        return EMPTY;
    }

    public static <T> Literal<T> getLiteralBlindly(T value) {
        return new Literal<T>(value);
    }

    public static Optional<Literal> getLiteral(String literal) {
        if (literal == null) {
            return Optional.of(empty());
        }

        // If there's quotes, it's a string
        if (literal.startsWith("\"") && literal.endsWith("\"")) {
            return Optional.<Literal>of(new Literal<String>(Utilities.unescape(literal.substring(1, literal.length() - 1))));
        }

        // Literal booleans are only true or false
        if (literal.equals("true")) {
            return Optional.<Literal>of(new Literal<Boolean>(true));
        }

        if (literal.equals("false")) {
            return Optional.<Literal>of(new Literal<Boolean>(false));
        }

        // Everything in literals are basically just numbers
        Integer intVal = Utilities.getInteger(literal);
        if (intVal != null) {
            return Optional.<Literal>of(new Literal<Integer>(intVal));
        }

        Long longVal = Utilities.getLong(literal);
        if (longVal != null) {
            return Optional.<Literal>of(new Literal<Long>(longVal));
        }

        Float floatVal = Utilities.getFloat(literal);
        if (floatVal != null) {
            return Optional.<Literal>of(new Literal<Float>(floatVal));
        }

        Double doubleVal = Utilities.getDouble(literal);
        if (doubleVal != null) {
            return Optional.<Literal>of(new Literal<Double>(doubleVal));
        }

        return Optional.absent();
    }

    public Optional<T> getValue() {
        return value;
    }

    public boolean isString() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get().getClass().isAssignableFrom(String.class);
    }

    public boolean isBoolean() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get().getClass().isAssignableFrom(Boolean.class);
    }

    public boolean isInteger() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get().getClass().isAssignableFrom(Integer.class);
    }

    public boolean isLong() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get().getClass().isAssignableFrom(Long.class);
    }

    public boolean isIntegral() {
        return isInteger() || isLong();
    }

    public boolean isFloat() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get().getClass().isAssignableFrom(Float.class);
    }

    public boolean isDouble() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get().getClass().isAssignableFrom(Double.class);
    }

    public boolean isDecimal() {
        return isFloat() || isDouble();
    }

    public boolean isNumber() {
        return isIntegral() || isDecimal();
    }

    // Some nice literal to literal conversions
    public Literal<Integer> parseInteger() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Integer>(Integer.parseInt((String) value.get()));
    }

    public Literal<Long> parseLong() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Long>(Long.parseLong((String) value.get()));
    }

    public Literal<Float> parseFloat() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Float>(Float.parseFloat((String) value.get()));
    }

    public Literal<Double> parseDouble() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Double>(Double.parseDouble((String) value.get()));
    }

    public Literal<Boolean> parseBoolean() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Boolean>(Boolean.parseBoolean((String) value.get()));
    }

    public Literal<String> parseString() {
        checkState(value.isPresent(), "This literal must be present to do this");
        return new Literal<String>(value.get().toString());
    }

    public String getString() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isString(), "This literal is not a string");
        return (String) value.get();
    }

    public Boolean getBoolean() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isBoolean(), "This literal is not a boolean");
        return (Boolean) value.get();
    }

    public Integer getInteger() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isInteger(), "This literal is not a integer");
        return (Integer) value.get();
    }

    public Long getLong() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isLong(), "This literal is not a long");
        return (Long) value.get();
    }

    public Float getFloat() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isFloat(), "This literal is not a float");
        return (Float) value.get();
    }

    public Double getDouble() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isDouble(), "This literal is not a double");
        return (Double) value.get();
    }
}
