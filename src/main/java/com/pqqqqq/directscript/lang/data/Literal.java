package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.util.StringParser;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.apache.commons.lang3.StringEscapeUtils;
import org.spongepowered.api.entity.player.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A literal is a value that is not dependent on any environment; a constant
 */
public class Literal<T> {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");

    private static final Literal EMPTY = new Literal();
    private static final Literal<Boolean> TRUE = new Literal(true);
    private static final Literal<Boolean> FALSE = new Literal(false);
    private static final Literal<String> NULL = new Literal("null");

    private final Optional<T> value;
    private final boolean normalized;

    private Literal() {
        this(null);
    }

    private Literal(T value) {
        this(value, false);
    }

    private Literal(T value, boolean normalized) {
        this.value = Optional.fromNullable(value);
        this.normalized = normalized;
    }

    public static Literal empty() {
        return EMPTY;
    }

    public static Literal<Boolean> trueLiteral() {
        return TRUE;
    }

    public static Literal<Boolean> falseLiteral() {
        return FALSE;
    }

    public static Literal<String> nullLiteral() {
        return NULL;
    }

    public static <T> Literal getLiteralBlindly(T value) {
        if (value == null) {
            return Literal.empty();
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Float) {
            return new Literal<Double>(Double.parseDouble(value.toString()));
        }

        if (value.getClass().isArray()) { // Special for arrays
            List<Variable> array = new ArrayList<Variable>();
            for (Object obj : (Object[]) value) {
                array.add(new Variable(null, Literal.getLiteralBlindly(obj)));
            }
            return new Literal<List<Variable>>(array);
        }

        return new Literal<T>(value);
    }

    public static Optional<Literal> getLiteral(ScriptInstance scriptInstance, String literal) {
        if (literal == null || literal.isEmpty() || literal.equals("null")) { // Null or empty values return an empty literal
            return Optional.of(empty());
        }

        // If there's [], it's an array
        if (literal.startsWith("[") && literal.endsWith("]")) {
            List<Variable> array = new ArrayList<Variable>(); // Max size of list is 1000

            int index = 0;
            for (String arrayValue : StringParser.instance().parseSplit(literal.substring(1, literal.length() - 1), ",")) {
                array.add(new Variable(null, scriptInstance.getSequencer().parse(arrayValue)));
            }
            return Optional.<Literal>of(new Literal<List<Variable>>(array));
        }

        // If there's quotes, it's a string
        if (literal.startsWith("\"") && literal.endsWith("\"")) {
            return Optional.<Literal>of(new Literal<String>(StringEscapeUtils.unescapeJava(literal.substring(1, literal.length() - 1))));
        }

        // Literal booleans are only true or false
        if (literal.equals("true")) {
            return Optional.<Literal>of(TRUE);
        }

        if (literal.equals("false")) {
            return Optional.<Literal>of(FALSE);
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

    public boolean isNormalized() {
        return normalized || !isString() && !isArray();
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

    public boolean isArray() {
        checkState(value.isPresent(), "This literal must be present to check this");
        return value.get() instanceof List;
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
        if (!value.isPresent()) {
            return NULL;
        }

        // Make integers not have the .0
        if (isNumber()) {
            return new Literal<String>(decimalFormat.format(getNumber()));
        }

        // Format arrays
        if (isArray()) {
            String string = "[";
            List<Variable> array = getArray();

            for (Variable variable : array) {
                string += variable.getData().getString() + ", ";
            }
            return new Literal<String>((array.isEmpty() ? string : string.substring(0, string.length() - 2)) + "]");
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

    public List<Variable> getArray() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isArray(), "This literal is not a literal");
        return (List<Variable>) value.get();
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

        if (isString() || other.isString()) {
            return Literal.getLiteralBlindly(getString() + other.getString()); // Everything can be a string
        }

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() + other.getNumber());
            }
        }

        return Literal.getLiteralBlindly(getString() + other.getString());
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

    public Literal mult(Literal other) {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(other.getValue().isPresent(), "This literal must be present to do this");

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() * other.getNumber());
            }
        }

        throw new IllegalArgumentException(other.getValue().get().getClass().getName() + " cannot be multiplied by " + value.get().getClass().getName());
    }

    public Literal div(Literal other) {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(other.getValue().isPresent(), "This literal must be present to do this");

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() / other.getNumber());
            }
        }

        throw new IllegalArgumentException(other.getValue().get().getClass().getName() + " cannot be divided by " + value.get().getClass().getName());
    }

    public Literal<Boolean> negative() {
        checkState(value.isPresent(), "This literal must be present to do this");
        checkState(isBoolean(), "Negation can only be done to booleans");
        return new Literal<Boolean>(!getBoolean());
    }

    public Literal normalize() {
        checkState(value.isPresent(), "This literal must be present to do this");

        if (!isNormalized()) {
            if (isString()) {
                return new Literal<String>("\"" + getString() + "\"", true);
            }
            if (isArray()) {
                for (Variable var : getArray()) {
                    Literal data = var.getData();
                    if (!data.isEmpty() && !data.isNormalized()) {
                        var.setData(data.normalize());
                    }
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return getString();
    }
}
