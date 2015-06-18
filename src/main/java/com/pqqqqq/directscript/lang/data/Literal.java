package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
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
 * @param <T> the literal type
 */
public class Literal<T> implements DataContainer<T> {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");

    private final Optional<T> value;

    Literal() {
        this(null);
    }

    Literal(T value) {
        this.value = Optional.fromNullable(value);
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
            return Literals.EMPTY;
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? Literals.TRUE : Literals.FALSE; // No need to create more immutable instances
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Float) {
            return new Literal<Double>(Double.parseDouble(value.toString()));
        }

        if (value.getClass().isArray()) { // Special for arrays
            List<LiteralHolder> array = new ArrayList<LiteralHolder>();
            for (Object obj : (Object[]) value) {
                array.add(new LiteralHolder(Literal.getLiteralBlindly(obj)));
            }
            return new Literal<List<LiteralHolder>>(array);
        }

        return new Literal<T>(value);
    }

    /**
     * Creates an {@link Optional} {@link Literal} by parsing it through the {@link Sequencer}
     *
     * @param literal        the string to parse
     * @return the new literal, or {@link Optional#absent()} if the literal cannot be parsed
     */
    public static Optional<Literal> getLiteral(String literal) {
        if (literal == null || literal.isEmpty() || literal.equals("null")) { // Null or empty values return an empty parse
            return Optional.of(Literals.EMPTY);
        }

        // If there's quotes, it's a string
        if (literal.startsWith("\"") && literal.endsWith("\"")) {
            return Optional.<Literal>of(new Literal<String>(Utilities.formatColour(StringEscapeUtils.unescapeJava(literal.substring(1, literal.length() - 1)))));
        }

        // Literal booleans are only true or false
        if (literal.equals("true")) {
            return Optional.<Literal>of(Literals.TRUE);
        }

        if (literal.equals("false")) {
            return Optional.<Literal>of(Literals.FALSE);
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
        return !getValue().isPresent();
    }

    /**
     * Gets the {@link Optional} generic value for this {@link Literal}
     * @return the value
     */
    public Optional<T> getValue() {
        return value;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a String
     * @return true if a String
     */
    public boolean isString() {
        checkState(getValue().isPresent(), "This parse must be present to check this");
        return getValue().get() instanceof String;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a Boolean
     * @return true if a Boolean
     */
    public boolean isBoolean() {
        checkState(getValue().isPresent(), "This parse must be present to check this");
        return getValue().get() instanceof Boolean;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a Number
     * @return true if a Number
     */
    public boolean isNumber() {
        checkState(getValue().isPresent(), "This parse must be present to check this");
        return getValue().get() instanceof Double;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of an Array
     * @return true if an Array
     */
    public boolean isArray() {
        checkState(getValue().isPresent(), "This parse must be present to check this");
        return getValue().get() instanceof List;
    }

    /**
     * Gets the String value of this {@link Literal}
     * @return the String value
     */
    public String getString() {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        return isString() ? (String) getValue().get() : parseString().getString();
    }

    /**
     * Gets the Boolean value of this {@link Literal}
     * @return the Boolean value
     */
    public Boolean getBoolean() {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        return isBoolean() ? (Boolean) getValue().get() : parseBoolean().getBoolean();
    }

    /**
     * Gets the number (Double) value of this {@link Literal}
     * @return the number value
     */
    public Double getNumber() {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        return isNumber() ? (Double) getValue().get() : parseNumber().getNumber();
    }

    /**
     * Gets the array ({@link LiteralHolder} {@link List}) value of this {@link Literal}
     * @return the array value
     */
    public List<LiteralHolder> getArray() {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        return isArray() ? (List<LiteralHolder>) getValue().get() : parseArray().getArray();
    }

    /**
     * Gets the specific {@link LiteralHolder} array data at the index
     * @param index the index for the array
     * @return the variable at this index
     */
    public LiteralHolder getArrayValue(int index) {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        checkState(isArray(), "This parse is not an array");

        List<LiteralHolder> literalHolders = getArray();
        Utilities.buildToIndex(literalHolders, index, new LiteralHolder());
        return literalHolders.get(index);
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
        if (isNumber() && other.isNumber()) {
            return Literal.getLiteralBlindly(getNumber() + other.getNumber());
        }

        return Literal.getLiteralBlindly(getString() + other.getString()); // Everything can be a string
    }

    /**
     * Subtracts a {@link Literal} from this one numerically
     * @param other the other literal
     * @return the difference literal
     */
    public Literal sub(Literal other) {
        return Literal.getLiteralBlindly(getNumber() - other.getNumber());
    }

    /**
     * Multiplies a {@link Literal} by this one numerically
     * @param other the other literal
     * @return the product literal
     */
    public Literal mult(Literal other) {
        return Literal.getLiteralBlindly(getNumber() * other.getNumber());
    }

    /**
     * Divides a {@link Literal} by this one numerically
     * @param other the other literal
     * @return the quotient literal
     */
    public Literal div(Literal other) {
        return Literal.getLiteralBlindly(getNumber() / other.getNumber());
    }

    /**
     * Raises a {@link Literal} to the power of this one numerically
     *
     * @param other the other literal
     * @return the resultant literal
     */
    public Literal pow(Literal other) {
        return Literal.getLiteralBlindly(Math.pow(getNumber(), other.getNumber()));
    }

    /**
     * Takes the nth root as per a {@link Literal} of this one numerically
     *
     * @param other the other literal
     * @return the resultant literal
     */
    public Literal root(Literal other) {
        return Literal.getLiteralBlindly(Math.pow(getNumber(), (1D / other.getNumber())));
    }

    /**
     * Negates this {@link Literal} by switching the boolean value (true -> false, false -> true)
     * @return the negative boolean literal
     */
    public Literal<Boolean> negative() {
        checkState(isBoolean(), "Negation can only be done to booleans (" + getString() + ")");
        return new Literal<Boolean>(!getBoolean());
    }

    /**
     * Gets a {@link Literal} with the specified new value if this literal is {@link Literals#EMPTY}, or otherwise this literal
     * @param newvalue the new value
     * @return this literal if not empty, or a literal with newvalue
     */
    public Literal or(Object newvalue) {
        if (isEmpty()) {
            return Literal.getLiteralBlindly(newvalue);
        }
        return this;
    }

    public Literal<T> resolve(ScriptInstance scriptInstance) { // Override for DataContainer
        return this;
    }

    // Object overrides

    @Override
    public String toString() {
        return getString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Literal && getValue().equals(((Literal) obj).getValue());
    }

    // Parsing stuff for conversions
    private Literal<String> parseString() {
        if (!getValue().isPresent()) {
            return Literals.EMPTY;
        }

        // Make integers not have the .0
        if (isNumber()) {
            return new Literal<String>(decimalFormat.format(getNumber()));
        }

        // Format arrays
        if (isArray()) {
            String string = "[";
            List<LiteralHolder> array = getArray();
            for (LiteralHolder literalHolder : array) {
                string += literalHolder.getData().getString() + ", ";
            }
            return new Literal<String>((array.isEmpty() ? string : string.substring(0, string.length() - 2)) + "]");
        }

        return new Literal<String>(getValue().get().toString());
    }

    private Literal<Boolean> parseBoolean() {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Boolean>(Boolean.parseBoolean((String) getValue().get()));
    }

    private Literal<Double> parseNumber() {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        checkState(isString(), "The value must be a string to use this method");

        return new Literal<Double>(Double.parseDouble((String) getValue().get()));
    }

    private Literal<List<LiteralHolder>> parseArray() {
        checkState(getValue().isPresent(), "This parse must be present to do this");
        return new Literal<List<LiteralHolder>>(new ArrayList<LiteralHolder>(Arrays.asList(new LiteralHolder[]{new LiteralHolder(this)}))); // Create a singleton of the data
    }
}
