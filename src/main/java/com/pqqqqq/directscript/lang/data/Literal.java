package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.util.StringUtil;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.entity.player.Player;

import java.text.DecimalFormat;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A literal is a value that is not dependent on any environment; a constant
 */
public class Literal<T> {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

    private static final Literal EMPTY = new Literal();
    private static final Literal TRUE = new Literal(true);
    private static final Literal FALSE = new Literal(false);

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

    public static Literal trueLiteral() {
        return TRUE;
    }

    public static Literal falseLiteral() {
        return FALSE;
    }

    public static <T> Literal getLiteralBlindly(T value) {
        if (value == null) {
            return Literal.empty();
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Float) {
            return new Literal<Double>(Double.parseDouble(value.toString()));
        }

        return new Literal<T>(value);
    }

    public static Optional<Literal> getLiteral(String literal) {
        if (literal == null) {
            return Optional.of(empty());
        }

        // If there's quotes, it's a string
        if (literal.startsWith("\"") && literal.endsWith("\"")) {
            return Optional.<Literal>of(new Literal<String>(StringUtil.unescape(literal.substring(1, literal.length() - 1))));
        }

        // Literal booleans are only true or false
        if (literal.equals("true")) {
            return Optional.of(TRUE);
        }

        if (literal.equals("false")) {
            return Optional.of(FALSE);
        }

        // Everything in literals are basically just numbers, just make them all doubles
        Double doubleVal = Utilities.getDouble(literal);
        if (doubleVal != null) {
            return Optional.<Literal>of(new Literal<Double>(doubleVal));
        }

        return Optional.absent();
    }

    public boolean isEmpty() {
        return !value.isPresent();
    }

    public Optional<T> getValue() {
        return value;
    }

    public boolean isString() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get() instanceof String;
    }

    public boolean isBoolean() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get() instanceof Boolean;
    }

    public boolean isNumber() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get() instanceof Double;
    }

    // Some nice literal to literal conversions
    public Literal<Double> parseNumber() {
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

        // Make integers not have the .0
        if (isNumber()) {
            return new Literal<String>(decimalFormat.format(getNumber()));
        }

        return new Literal<String>(value.get().toString());
    }

    public String getString() {
        checkState(value.isPresent(), "This literal must be present to do this");
        return value.get() instanceof String ? (String) value.get() : parseString().getString();
    }

    public Boolean getBoolean() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isBoolean(), "This literal is not a boolean");
        return (Boolean) value.get();
    }

    public Double getNumber() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isNumber(), "This literal is not a number");
        return (Double) value.get();
    }

    // Some common additional getters (sponge)
    public Optional<Player> getPlayer() {
        try {
            Optional<Player> playerOptional = DirectScript.instance().getGame().getServer().getPlayer(getString()); // Check name first
            if (playerOptional.isPresent()) {
                return playerOptional;
            }

             return DirectScript.instance().getGame().getServer().getPlayer(UUID.fromString(getString())); // Check uuid now
         } catch (IllegalArgumentException e) {
            return Optional.absent();
         }
    }

    // Arithmetic
    public Literal add(Literal other) {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(other.getValue().isPresent(), "This literal must be present to do this");

        if (isString()) {
            return Literal.getLiteralBlindly(getString() + other.parseString().getString()); // Everything can be a string
        }

        if (other.isString()) {
            return Literal.getLiteralBlindly(parseString().getString() + other.getString()); // Everything can be a string
        }

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() + other.getNumber());
            }
        }

        return Literal.getLiteralBlindly(parseString().getString() + other.parseString().getString());
        //throw new IllegalArgumentException(other.getValue().get().getClass().getName() + " cannot be added to " + value.get().getClass().getName());
    }

    public Literal sub(Literal other) {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(other.getValue().isPresent(), "This literal must be present to do this");

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() - other.getNumber());
            }
        }

        throw new IllegalArgumentException(other.getValue().get().getClass().getName() + " cannot be subtraced from " + value.get().getClass().getName());
    }
}
