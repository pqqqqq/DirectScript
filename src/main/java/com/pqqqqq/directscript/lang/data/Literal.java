package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.util.StringParser;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.apache.commons.lang3.StringEscapeUtils;
import org.spongepowered.api.entity.player.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A literal is an immutable value that is not dependent on any environment; a constant
 */
public class Literal<T> {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");

    private static final Literal EMPTY = new Literal();
    private static final Literal<Boolean> TRUE = new Literal(true);
    private static final Literal<Boolean> FALSE = new Literal(false);

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

    /**
     * An empty {@link Literal}, where the value is absent
     *
     * @return the empty literal
     */
    public static Literal empty() {
        return EMPTY;
    }

    /**
     * A true {@link Literal}, where the value is true (or 1)
     *
     * @return the true literal
     */
    public static Literal<Boolean> trueLiteral() {
        return TRUE;
    }

    /**
     * A false {@link Literal}, where the value is false (or 0)
     *
     * @return the false literal
     */
    public static Literal<Boolean> falseLiteral() {
        return FALSE;
    }

    /**
     * Blindly creates a {@link Literal} without parsing through the {@link Sequencer}
     *
     * @param value the value for the literal
     * @param <T>   the type parameter for the value
     * @return the new literal instance
     */
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

    /**
     * Creates an {@link Optional} {@link Literal} by parsing it through the {@link Sequencer}
     *
     * @param scriptInstance the {@link ScriptInstance} for the sequencer
     * @param literal        the string to parse
     * @return the new literal, or {@link Optional#absent()} if the literal cannot be parsed
     */
    public static Optional<Literal> getLiteral(ScriptInstance scriptInstance, String literal) {
        if (literal == null || literal.isEmpty() || literal.equals("null")) { // Null or empty values return an empty parse
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
            return Optional.<Literal>of(new Literal<String>(Utilities.formatColour(StringEscapeUtils.unescapeJava(literal.substring(1, literal.length() - 1)))));
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

    /**
     * Gets whether this literal is empty ({@link #getValue()} ={@link Optional#absent()})
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return !value.isPresent();
    }

    /**
     * Gets the {@link Optional} generic value for this {@link Literal}
     * @return the value
     */
    public Optional<T> getValue() {
        return value;
    }

    /**
     * Gets whether this {@link Literal} is normalized, or has been surrounded by strings
     * @return true if normalized
     */
    public boolean isNormalized() {
        return normalized || !isString() && !isArray();
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a String
     * @return true if a String
     */
    public boolean isString() {
        checkState(value.isPresent(), "This parse must be present to check this");
        return value.get() instanceof String;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a Boolean
     * @return true if a Boolean
     */
    public boolean isBoolean() {
        checkState(value.isPresent(), "This parse must be present to check this");
        return value.get() instanceof Boolean;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a Number
     * @return true if a Number
     */
    public boolean isNumber() {
        checkState(value.isPresent(), "This parse must be present to check this");
        return value.get() instanceof Double;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of an Array
     * @return true if an Array
     */
    public boolean isArray() {
        checkState(value.isPresent(), "This parse must be present to check this");
        return value.get() instanceof List;
    }

    /**
     * Gets the String value of this {@link Literal}
     * @return the String value
     */
    public String getString() {
        checkState(value.isPresent(), "This parse must be present to do this");
        return isString() ? (String) value.get() : parseString().getString();
    }

    /**
     * Gets the Boolean value of this {@link Literal}
     * @return the Boolean value
     */
    public Boolean getBoolean() {
        checkState(value.isPresent(), "This parse must be present to do this");
        return isBoolean() ? (Boolean) value.get() : parseBoolean().getBoolean();
    }

    /**
     * Gets the number (Double) value of this {@link Literal}
     * @return the number value
     */
    public Double getNumber() {
        checkState(value.isPresent(), "This parse must be present to do this");
        return isNumber() ? (Double) value.get() : parseNumber().getNumber();
    }

    /**
     * Gets the array ({@link Variable} {@link List}) value of this {@link Literal}
     * @return the array value
     */
    public List<Variable> getArray() {
        checkState(value.isPresent(), "This parse must be present to do this");
        return isArray() ? (List<Variable>) value.get() : parseArray().getArray();
    }

    /**
     * Gets the specific {@link Variable} array data at the index
     * @param index the index for the array
     * @return the variable at this index
     */
    public Variable getArrayValue(int index) {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(isArray(), "This parse is not an array");

        List<Variable> variableList = getArray();
        Utilities.buildToIndex(variableList, index, Variable.empty());
        return variableList.get(index);
    }

    // Some common additional getters (sponge)

    /**
     * Gets an {@link Optional} {@link Player} by checking both the names and UUIDS of players in the server
     * @return the player
     */
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

    /**
     * Adds a {@link Literal} to this one, either by string (concatenation) or numerically
     * @param other the other literal
     * @return the sum literal
     */
    public Literal add(Literal other) {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(other.getValue().isPresent(), "This parse must be present to do this");

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

    /**
     * Subtracts a {@link Literal} from this one numerically
     * @param other the other literal
     * @return the difference literal
     */
    public Literal sub(Literal other) {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(other.getValue().isPresent(), "This parse must be present to do this");

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() - other.getNumber());
            }
        }

        throw new IllegalArgumentException(other.getValue().get().getClass().getName() + " cannot be subtraced from " + value.get().getClass().getName());
    }

    /**
     * Multiplies a {@link Literal} by this one numerically
     * @param other the other literal
     * @return the product literal
     */
    public Literal mult(Literal other) {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(other.getValue().isPresent(), "This parse must be present to do this");

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() * other.getNumber());
            }
        }

        throw new IllegalArgumentException(other.getValue().get().getClass().getName() + " cannot be multiplied by " + value.get().getClass().getName());
    }

    /**
     * Divides a {@link Literal} by this one numerically
     * @param other the other literal
     * @return the quotient literal
     */
    public Literal div(Literal other) {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(other.getValue().isPresent(), "This parse must be present to do this");

        if (isNumber()) {
            if (other.isNumber()) {
                return Literal.getLiteralBlindly(getNumber() / other.getNumber());
            }
        }

        throw new IllegalArgumentException(other.getValue().get().getClass().getName() + " cannot be divided by " + value.get().getClass().getName());
    }

    /**
     * Negates this {@link Literal} by switching the boolean value (true -> false, false -> true)
     * @return the negative boolean literal
     */
    public Literal<Boolean> negative() {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(isBoolean(), "Negation can only be done to booleans");
        return new Literal<Boolean>(!getBoolean());
    }

    /**
     * Normalizes this {@link Literal} by adding quotes where applicable,
     * @return the normalized literal
     */
    public Literal normalize() {
        checkState(value.isPresent(), "This parse must be present to do this");

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

    /**
     * Gets a {@link Literal} with the specified new value if this literal is {@link #empty()}, or otherwise this literal
     * @param newvalue the new value
     * @return this literal if not empty, or a literal with newvalue
     */
    public Literal or(Object newvalue) {
        if (isEmpty()) {
            return Literal.getLiteralBlindly(newvalue);
        }
        return this;
    }

    // Object overrides

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Literal && value.equals(((Literal) obj).getValue());
    }

    // Parsing stuff for conversions
    private Literal<String> parseString() {
        if (!value.isPresent()) {
            return Literal.empty();
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

    private Literal<Boolean> parseBoolean() {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Boolean>(Boolean.parseBoolean((String) value.get()));
    }

    private Literal<Double> parseNumber() {
        checkState(value.isPresent(), "This parse must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Double>(Double.parseDouble((String) value.get()));
    }

    private Literal<List<Variable>> parseArray() {
        checkState(value.isPresent(), "This parse must be present to do this");
        return new Literal<List<Variable>>(new ArrayList<Variable>(Arrays.asList(new Variable[]{new Variable(null, this)}))); // Create a singleton of the data
    }
}
