package com.pqqqqq.directscript.lang.data;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.container.AmnesiacContainer;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.data.container.expression.ArithmeticContainer;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.apache.commons.lang3.StringEscapeUtils;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A literal is an immutable value that is not dependent on any environment; a constant
 *
 * @param <T> the literal type
 */
public class Literal<T> implements Datum<T> {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.###");

    private final Optional<T> value;

    protected Literal() {
        this(null);
    }

    protected Literal(T value) {
        this.value = Optional.ofNullable(value);
    }

    /**
     * Creates a {@link Literal} without parsing through the {@link Sequencer} and using the plain object
     *
     * @param value the value for the literal
     * @return the new literal instance
     */
    public static <T> Literal<T> fromObject(Object value) {
        if (value == null) {
            return Literals.EMPTY;
        }

        if (value instanceof Literal) {
            return (Literal) value;
        }

        ObjectiveLiteral objectiveLiteral = ObjectiveLiteral.of(value); // If it's an objective literal, return it
        if (objectiveLiteral != null) {
            return objectiveLiteral;
        }

        if (value instanceof String) {
            String string = (String) value;
            if (string.isEmpty()) {
                return (Literal<T>) Literals.EMPTY_STRING;
            } else {
                return (Literal<T>) new Literal<>(Utilities.formatColour(string));
            }
        }

        if (value instanceof Boolean) {
            return (Literal<T>) ((Boolean) value ? Literals.TRUE : Literals.FALSE); // No need to create more immutable instances
        }

        if (value instanceof Integer || value instanceof Long || value instanceof Float) {
            double number = Double.parseDouble(value.toString());
            if (number == 0D) {
                return (Literal<T>) Literals.ZERO;
            } else if (number == 1D) {
                return (Literal<T>) Literals.ONE;
            } else {
                return (Literal<T>) new Literal<>(number);
            }
        }

        if (value.getClass().isArray()) { // Special for arrays
            List<Literal> array = new ArrayList<>();
            for (Object obj : (Object[]) value) {
                array.add(obj instanceof Literal ? (Literal) obj : Literal.fromObject(obj));
            }

            if (array.isEmpty()) {
                return (Literal<T>) Literals.EMPTY_MAP;
            }

            return (Literal<T>) new Literal<>(ImmutableList.copyOf(array));
        }

        if (value instanceof Collection) { // Special for collections
            List<Literal> array = new ArrayList<>();
            for (Object obj : (Collection) value) {
                array.add(obj instanceof Literal ? (Literal) obj : Literal.fromObject(obj));
            }

            if (array.isEmpty()) {
                return (Literal<T>) Literals.EMPTY_ARRAY;
            }

            return (Literal<T>) new Literal<>(ImmutableList.copyOf(array));
        }

        if (value instanceof Map) { // Special for maps
            Map<Literal, Literal> map = new HashMap<>();
            for (Map.Entry entry : ((Map<Object, Object>) value).entrySet()) {
                map.put(entry.getKey() instanceof Literal ? (Literal) entry.getKey() : Literal.fromObject(entry.getKey()), entry.getValue() instanceof Literal ? (Literal) entry.getValue() : Literal.fromObject(entry.getValue()));
            }

            if (map.isEmpty()) {
                return (Literal<T>) Literals.EMPTY_MAP;
            }

            return (Literal<T>) new Literal<>(ImmutableMap.copyOf(map));
        }

        return new Literal(value);
    }

    protected static <T> Optional<Literal<T>> fromSequence(String literal) { // Only Sequencer should use this
        if (literal == null || literal.isEmpty() || literal.equals("null")) { // Null or empty values return an empty parse
            return Optional.of(Literals.EMPTY);
        }

        // If there's quotes, it's a string
        if (literal.startsWith("\"") && literal.endsWith("\"")) {
            return Optional.of(Literal.fromObject(Utilities.formatColour(StringEscapeUtils.unescapeJava(literal.substring(1, literal.length() - 1)))));
        }

        // Literal booleans are only true or false
        if (literal.equals("true")) {
            return Optional.of((Literal<T>) Literals.TRUE);
        }

        if (literal.equals("false")) {
            return Optional.of((Literal<T>) Literals.FALSE);
        }

        // All numbers are doubles, just make them all doubles
        Double doubleVal = Utilities.getDouble(literal);
        if (doubleVal != null) {
            return Optional.of(Literal.fromObject(doubleVal));
        }

        return Optional.empty();
    }

    /**
     * Gets whether this literal is empty ({@link #getValue()} = {@link Optional#empty()} ()})
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return !getValue().isPresent();
    }

    /**
     * Gets the {@link Optional} generic value for this {@link Literal}
     *
     * @return the value
     */
    public Optional<T> getValue() {
        return value;
    }

    /**
     * Gets the {@link Optional} {@link Datum} that this literal was resolved from, by {@link DataContainer#resolve(Context)}
     * @return the value
     */
    public Optional<Datum> getResolvedFrom() {
        return Optional.empty();
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a String
     *
     * @return true if a String
     */
    public boolean isString() {
        return getValue().isPresent() && getValue().get() instanceof String;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a Boolean
     *
     * @return true if a Boolean
     */
    public boolean isBoolean() {
        return getValue().isPresent() && getValue().get() instanceof Boolean;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a Number
     *
     * @return true if a Number
     */
    public boolean isNumber() {
        return getValue().isPresent() && getValue().get() instanceof Double;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of an Array
     *
     * @return true if an Array
     */
    public boolean isArray() {
        return getValue().isPresent() && getValue().get() instanceof List;
    }

    /**
     * Gets whether this {@link Literal}'s value is an instance of a Map
     * @return true if a Map
     */
    public boolean isMap() {
        return getValue().isPresent() && getValue().get() instanceof Map;
    }

    /**
     * Gets whether this {@link Literal} is objective, which is an instance of {@link ObjectiveLiteral}
     * @return true if objective
     */
    public boolean isObjective() {
        return false;
    }

    /**
     * Gets the String value of this {@link Literal}
     *
     * @return the String value
     */
    public String getString() {
        return isString() ? (String) getValue().get() : parseString().getString();
    }

    /**
     * Gets the Boolean value of this {@link Literal}
     *
     * @return the Boolean value
     */
    public Boolean getBoolean() {
        return isBoolean() ? (Boolean) getValue().get() : parseBoolean().getBoolean();
    }

    /**
     * Gets the number (Double) value of this {@link Literal}
     *
     * @return the number value
     */
    public Double getNumber() {
        return isNumber() ? (Double) getValue().get() : parseNumber().getNumber();
    }

    /**
     * Gets the array ({@link Literal} {@link List}) value of this {@link Literal}
     *
     * @return the array value
     */
    public List<Literal> getArray() {
        return isArray() ? (List<Literal>) getValue().get() : parseArray().getArray();
    }

    /**
     * Gets the map ({@link Literal} K-V {@link Map}) value of this {@link Literal}
     * @return the map value
     */
    public Map<Literal, Literal> getMap() {
        return isMap() ? (Map<Literal, Literal>) getValue().get() : parseMap().getMap();
    }

    /**
     * Gets the specific {@link Literal} array data at the index
     *
     * @param index the index for the array (base 1)
     * @return the variable at this index
     */
    public Literal getArrayValue(int index) {
        checkState(isArray(), "This literal is not an array");
        return getArray().get(--index);
    }

    /**
     * Gets a {@link Literal} map data from its key
     *
     * @param data the key value
     * @return the map value, or null
     */
    public Literal getMapValue(Literal data) {
        checkState(isMap(), "This literal is not a map");
        return getMap().get(data);
    }

    // Sponge casting

    /**
     * Gets the {@link Literal} as a specific given type
     * @param type the type
     * @param <R> the generic type parameter
     * @return the optional value of the type
     */
    @SuppressWarnings("unchecked")
    public <R> Optional<R> getAs(Class<R> type) {
        try {
            if (Player.class.isAssignableFrom(type) || type.isAssignableFrom(Player.class)) {
                Optional<Player> playerOptional = DirectScript.instance().getGame().getServer().getPlayer(getString()); // Check name first
                if (playerOptional.isPresent()) {
                    return (Optional<R>) playerOptional;
                }

                return (Optional<R>) DirectScript.instance().getGame().getServer().getPlayer(UUID.fromString(getString())); // Check uuid now
            } else if (World.class.isAssignableFrom(type)) {
                return (Optional<R>) DirectScript.instance().getGame().getServer().getWorld(getString());
            } else if (Vector3d.class.isAssignableFrom(type)) {
                List<Literal> array = getArray();
                return (Optional<R>) Optional.of(new Vector3d(array.get(0).getNumber(), array.get(1).getNumber(), array.get(2).getNumber()));
            } else if (Vector3f.class.isAssignableFrom(type)) {
                List<Literal> array = getArray();
                return (Optional<R>) Optional.of(new Vector3f(array.get(0).getNumber(), array.get(1).getNumber(), array.get(2).getNumber()));
            } else if (Vector3i.class.isAssignableFrom(type)) {
                List<Literal> array = getArray();
                return (Optional<R>) Optional.of(new Vector3i(array.get(0).getNumber(), array.get(1).getNumber(), array.get(2).getNumber()));
            } else if (Location.class.isAssignableFrom(type) || BlockSnapshot.class.isAssignableFrom(type) || BlockState.class.isAssignableFrom(type)) {
                Location loc;

                if (isMap()) {
                    Map<Literal, Literal> map = getMap();

                    World world = DirectScript.instance().getGame().getServer().getWorld(Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("world")).getString()).get();
                    Vector3d vec = new Vector3d(Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("x")).getNumber(), Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("y")).getNumber(), Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("z")).getNumber());
                    loc = new Location(world, vec);
                } else {
                    List<Literal> array = getArray();

                    World world = DirectScript.instance().getGame().getServer().getWorld(array.get(0).getString()).get();
                    Vector3d vec = new Vector3d(array.get(1).getNumber(), array.get(2).getNumber(), array.get(3).getNumber());
                    loc = new Location(world, vec);
                }

                if (Location.class.isAssignableFrom(type)) {
                    return (Optional<R>) Optional.of(loc);
                } else if (BlockState.class.isAssignableFrom(type)) {
                    return (Optional<R>) Optional.of(loc.getBlock());
                } else if (BlockSnapshot.class.isAssignableFrom(type)) {
                    return (Optional<R>) Optional.of(loc.createSnapshot());
                }
            } else if (ItemStack.class.isAssignableFrom(type)) {
                Map<Literal, Literal> map = getMap();
                ItemType itemType = Utilities.getType(ItemType.class, Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("type")).getString()).get();
                int quantity = Optional.ofNullable(Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("amount"))).orElse(Literals.ONE).getNumber().intValue();

                ItemStack itemStack = DirectScript.instance().getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(itemType).quantity(quantity).build();

                Literal displayName = Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("displayName"));
                Literal lore = Utilities.getMapType(map, (literal) -> literal.getString().equalsIgnoreCase("lore"));

                if (displayName != null) {
                    itemStack.offer(Keys.DISPLAY_NAME, Utilities.getText(displayName.getString()));
                }

                if (lore != null) {
                    List<Text> loreList = new ArrayList<>();
                    List<Literal> loreLiterals = lore.getArray();

                    loreLiterals.forEach((literal) -> loreList.add(Utilities.getText(literal.getString())));
                    itemStack.offer(Keys.ITEM_LORE, loreList);
                }

                return (Optional<R>) Optional.of(itemStack);
            } else if (CatalogType.class.isAssignableFrom(type)) {
                String id = getString();
                if (!id.contains(":")) {
                    id = "minecraft:" + id;
                }

                for (CatalogType catalogType : DirectScript.instance().getGame().getRegistry().getAllOf((Class<? extends CatalogType>) type)) {
                    if (catalogType.getId().equalsIgnoreCase(id)) {
                        return (Optional<R>) Optional.of(catalogType);
                    }
                }

                return Optional.empty();
            } else if (Text.class.isAssignableFrom(type)) {
                return (Optional<R>) Optional.of(Utilities.getText(getString()));
            }
        } catch (Throwable e) {
            //throw new LiteralCastException(e, "%s cannot be cast to %s", getValue().toString(), type.getName());
        }
        return Optional.empty();
    }

    @Override
    public Object serialize() {
        if (getResolvedFrom().isPresent()) {
            return getResolvedFrom().get().serialize(); // If it was resolved from something, use that serialization
        }

        if (isEmpty()) {
            return "null";
        } else if (isString()) {
            return "\"" + getString() + "\"";
        } else if (isArray()) {
            String str = "";

            for (Literal literal : getArray()) {
                str += literal.serialize() + ", ";
            }

            return str.isEmpty() ? "{}" : "{" + str.substring(0, str.length() - 2) + "}";
        } else if (isMap()) {
            String str = "";

            for (Map.Entry<Literal, Literal> entry : getMap().entrySet()) {
                str += entry.getKey().serialize() + " : " + entry.getValue().serialize() + ", ";
            }

            return str.isEmpty() ? "{}" : "{" + str.substring(0, str.length() - 2) + "}";
        }

        return getString(); // Numbers and booleans
    }

    // Arithmetic

    /**
     * Adds a {@link Literal} to this one, either by string (concatenation) or numerically
     *
     * @param other the other literal
     * @return the sum literal
     */
    public <T> Literal<T> add(Literal<?> other) {
        if (isArray()) {
            List<Literal> array = Lists.newArrayList();
            array.addAll(getArray());

            if (other.isArray()) {
                array.addAll(other.getArray());
            } else {
                if (!other.isEmpty()) {
                    array.add(other);
                }
            }
            return Literal.fromObject(array);
        } else if (other.isArray()) {
            if (isArray()) {
                return other.add(this); // Just swap it around if they're both arrays, no need to rewrite code
            }
        } else if (isMap() && other.isMap()) {
            Map<Literal, Literal> map = Maps.newHashMap();
            map.putAll(getMap());
            map.putAll(other.getMap());

            return Literal.fromObject(map);
        }

        if (isNumber() && other.isNumber()) {
            return Literal.fromObject(getNumber() + other.getNumber());
        }

        return Literal.fromObject(getString() + other.getString()); // Everything can be a string
    }

    /**
     * Subtracts a {@link Literal} from this one numerically
     *
     * @param other the other literal
     * @return the difference literal
     */
    public Literal<Double> sub(Literal<?> other) {
        return Literal.fromObject(getNumber() - other.getNumber());
    }

    /**
     * Multiplies a {@link Literal} by this one numerically
     *
     * @param other the other literal
     * @return the product literal
     */
    public Literal<Double> mult(Literal other) {
        return Literal.fromObject(getNumber() * other.getNumber());
    }

    /**
     * Divides a {@link Literal} by this one numerically
     *
     * @param other the other literal
     * @return the quotient literal
     */
    public Literal<Double> div(Literal other) {
        return Literal.fromObject(getNumber() / other.getNumber());
    }

    /**
     * Raises a {@link Literal} to the power of this one numerically
     *
     * @param other the other literal
     * @return the resultant literal
     */
    public Literal<Double> pow(Literal other) {
        return Literal.fromObject(Math.pow(getNumber(), other.getNumber()));
    }

    /**
     * Takes the nth root as per a {@link Literal} of this one numerically
     *
     * @param other the other literal
     * @return the resultant literal
     */
    public Literal<Double> root(Literal other) {
        return Literal.fromObject(Math.pow(getNumber(), (1D / other.getNumber())));
    }

    /**
     * Takes the modulus of the other literal, its remainder in division
     *
     * @param other the other literal
     * @return the resultant literal
     */
    public Literal<Double> mod(Literal other) {
        return Literal.fromObject(getNumber() % other.getNumber());
    }

    public Literal arithmetic(Literal other, ArithmeticContainer.ArithmeticOperator operator) {
        switch (operator) {
            case ADDITION:
                return add(other);
            case SUBTRACTION:
                return sub(other);
            case MULTIPLICATION:
                return mult(other);
            case DIVISION:
                return div(other);
            case EXPONENTIAL:
                return pow(other);
            case ROOT:
                return root(other);
            case MODULUS:
                return mod(other);
            default:
                throw new IllegalStateException("Unknown arithmetic operator: " + operator);
        }
    }

    /**
     * Negates this {@link Literal} by switching the boolean value (true -> false, false -> true)
     *
     * @return the negative boolean literal
     */
    public Literal<Boolean> negate() {
        checkState(isBoolean(), "Negation can only be done to booleans (" + getString() + ")");
        return Literal.fromObject(!getBoolean());
    }

    /**
     * Gets the negative {@link Literal} number of this number
     * @return the negative number
     */
    public Literal<Double> negative() {
        checkState(isNumber(), "Negatives can only be retrieved from numbers (" + getString() + ")");
        return Literal.fromObject(-getNumber());
    }

    /**
     * Gets a {@link Literal} with the specified new value if this literal is {@link Literals#EMPTY}, or otherwise this literal
     *
     * @param newvalue the new value
     * @return this literal if not empty, or a literal with newvalue
     */
    public Literal<T> or(Object newvalue) {
        if (isEmpty()) {
            return Literal.fromObject(newvalue);
        }
        return this;
    }

    /**
     * Gets a {@link Literal} with the specified {@link Supplier} if this literal is {@link Literals#EMPTY}, or otherwise this literal
     *
     * @param supplier the supplier
     * @return this literal if not empty, or a literal with newvalue
     */
    public Literal<T> or(Supplier supplier) {
        if (isEmpty()) {
            return Literal.fromObject(supplier.get());
        }
        return this;
    }

    /**
     * Gets a {@link Literal} with the specified new value if this literal is {@link Literals#EMPTY}, or otherwise this literal
     *
     * @param newvalue the new value
     * @return this literal if not empty, or the other literal
     */
    public Literal<T> or(Literal newvalue) {
        if (isEmpty()) {
            return newvalue;
        }
        return this;
    }

    @Override
    public Literal<T> resolve(Context ctx) {
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
            return Literals.EMPTY_STRING; // Empty string as default
        }

        // If it's objective, use getAs
        if (isObjective()) {
            return Literal.fromObject(getAs(String.class).get());
        }

        // Make integers not have the .0
        if (isNumber()) {
            return Literal.fromObject(decimalFormat.format(getNumber()));
        }

        // Format arrays
        if (isArray()) {
            List<Literal> array = getArray();
            if (array.size() == 1) {
                return array.get(0);
            }

            String string = "{";
            for (Literal literal : array) {
                string += literal.getString() + ", ";
            }

            return Literal.fromObject((array.isEmpty() ? string : string.substring(0, string.length() - 2)) + "}");
        }

        // Format maps
        if (isMap()) {
            String string = "{";
            Map<Literal, Literal> map = getMap();

            for (Map.Entry<Literal, Literal> entry : map.entrySet()) {
                string += entry.getKey().getString() + " : " + entry.getValue().getString() + ", ";
            }

            return Literal.fromObject((map.isEmpty() ? string : string.substring(0, string.length() - 2)) + "}");
        }

        return Literal.fromObject(getValue().get().toString());
    }

    private Literal<Boolean> parseBoolean() {
        if (!getValue().isPresent()) {
            return Literals.FALSE; // False as default
        }

        // If it's objective, use getAs
        if (isObjective()) {
            return Literal.fromObject(getAs(Boolean.class).get());
        }

        // If it's an array, check if it's a singleton
        if (isArray()) {
            List<Literal> array = getArray();
            if (array.size() == 1) {
                return array.get(0);
            }
        }

        return Literal.fromObject(Boolean.parseBoolean(getString()));
    }

    private Literal<Double> parseNumber() {
        if (!getValue().isPresent()) {
            return Literals.ZERO; // Zero as default
        }

        // If it's objective, use getAs
        if (isObjective()) {
            return Literal.fromObject(getAs(Double.class).get());
        }

        // If it's an array, check if it's a singleton
        if (isArray()) {
            List<Literal> array = getArray();
            if (array.size() == 1) {
                return array.get(0);
            }
        }

        return Literal.fromObject(Double.parseDouble(getString()));
    }

    private Literal<List<Literal>> parseArray() {
        if (!getValue().isPresent() || isMap() && getMap().isEmpty()) { // Empty array is default
            return Literals.EMPTY_ARRAY;
        }

        return Literal.fromObject(new Literal[]{this}); // Create a singleton of the data
    }

    private Literal<Map<Literal, Literal>> parseMap() {
        if (!getValue().isPresent() || isArray() && getArray().isEmpty()) { // Empty map is default
            return Literals.EMPTY_MAP;
        }

        throw new IllegalStateException("Maps cannot be casted to and fro'");
    }

    /**
     * An enumeration of {@link Literal} types
     */
    public enum Types {
        /**
         * Represents a {@link String}
         */
        STRING("string", Literals.EMPTY_STRING, (datum) -> datum instanceof Literal && ((Literal) datum).isString()),

        /**
         * Represents a {@link Boolean}
         */
        BOOLEAN("boolean", Literals.FALSE, (datum) -> datum instanceof Literal && ((Literal) datum).isBoolean()),

        /**
         * Represent a {@link Double} number
         */
        NUMBER("number", Literals.ZERO, (datum) -> datum instanceof Literal && ((Literal) datum).isNumber()),

        /**
         * Represents a {@link List} array
         */
        ARRAY("array", Literals.EMPTY_ARRAY, (datum) -> datum instanceof Literal && ((Literal) datum).isArray()),

        /**
         * Represents a {@link Map}
         */
        MAP("map", Literals.EMPTY_MAP, (datum) -> datum instanceof Literal && ((Literal) datum).isMap()),

        /**
         * Represents an unclassified {@link Object}
         */
        OBJECT("object", Literals.EMPTY, (datum) -> datum instanceof Literal && ((Literal) datum).isObjective()),

        /**
         * Represents an {@link AmnesiacContainer}
         */
        AMNESIAC("amnesiac", Literals.EMPTY, (datum) -> datum instanceof AmnesiacContainer),

        /**
         * Represents an {@link ArithmeticContainer}
         */
        ARITHMETIC("arithmetic", Literals.EMPTY, (datum) -> datum instanceof ArithmeticContainer);


        private String name;
        private Literal empty;
        private Function<? super Datum, Boolean> function;

        Types(String name, Literal empty, Function<? super Datum, Boolean> function) {
            this.name = name;
            this.empty = empty;
            this.function = function;
        }

        /**
         * Gets the {@link Optional} type from the given name
         *
         * @param name the name
         * @return the type, or empty if none
         */
        public static Optional<Types> fromName(String name) {
            if (name == null || name.equalsIgnoreCase("null")) {
                return Optional.empty();
            }

            for (Types type : values()) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return Optional.of(type);
                }
            }

            return Optional.empty();
        }

        /**
         * Gets the name of this type
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the default empty {@link Literal} for this type
         *
         * @return the empty literal
         */
        public Literal getEmpty() {
            return empty;
        }

        /**
         * Gets the {@link Function} used to check compatibility
         *
         * @return the function
         */
        public Function<? super Datum, Boolean> getFunction() {
            return function;
        }

        /**
         * Gets if this type is compatible with the given {@link Datum}, or if it's empty
         * @param datum the datum to check against
         * @return true if compatible or empty
         */
        public boolean isCompatible(Datum datum) {
            return isCompatible(datum, true);
        }

        /**
         * Gets if this type is compatible with the given {@link Datum}
         *
         * @param datum      the datum to check against
         * @param allowEmpty whether this datum is compatible if its empty
         * @return true if compatible
         */
        public boolean isCompatible(Datum datum, boolean allowEmpty) {
            return allowEmpty && datum instanceof Literal && datum.tryLiteral().isEmpty() || function.apply(datum);
        }
    }

    /**
     * Created by Kevin on 2015-06-18.
     * A class of common {@link Literal}s
     */
    public static class Literals {
        /**
         * An empty {@link Literal}, where the value is absent
         */
        public static final Literal EMPTY = new Literal();

        /**
         * A true {@link Literal}, where the value is true (or 1)
         */
        public static final Literal<Boolean> TRUE = new Literal(true);

        /**
         * A false {@link Literal}, where the value is false (or 0)
         */
        public static final Literal<Boolean> FALSE = new Literal(false);

        /**
         * A {@link Literal} whose value is a number equal to 0
         */
        public static final Literal<Double> ZERO = new Literal(0D);

        /**
         * A {@link Literal} whose value is a number equal to 1
         */
        public static final Literal<Double> ONE = new Literal(1D);

        /**
         * A {@link Literal} whose value is an empty string <i>""</i>
         */
        public static final Literal<String> EMPTY_STRING = new Literal("");

        /**
         * A {@link Literal} whose value is an empty array
         */
        public static final Literal<List<Literal>> EMPTY_ARRAY = new Literal(ImmutableList.copyOf(new ArrayList<>()));

        /**
         * A {@link Literal} whose value is an empty map
         */
        public static final Literal<Map<Literal, Literal>> EMPTY_MAP = new Literal(ImmutableMap.copyOf(new HashMap<>()));
    }

    /**
     * A resolved literal; a {@link Literal} that was resolved by {@link DataContainer#resolve(Context)} and has a {@link Datum} resolver
     */
    public static class Resolved<T> extends Literal<T> {
        private final Optional<Datum> resolvedFrom;

        protected Resolved(Datum resolvedFrom) {
            this(null, resolvedFrom);
        }

        protected Resolved(T value, Datum resolvedFrom) {
            super(value);
            this.resolvedFrom = Optional.ofNullable(resolvedFrom);
        }

        /**
         * Creates a {@link Literal.Resolved} without parsing through the {@link Sequencer} and using the plain object
         *
         * @param value        the value for the literal
         * @param resolvedFrom the datum
         * @return the new resolved literal instance
         */
        public static Resolved fromObject(Object value, Datum resolvedFrom) {
            return fromObject(Literal.fromObject(value), resolvedFrom);
        }

        /**
         * Creates a {@link Literal.Resolved} without parsing through the {@link Sequencer} and using the plain object
         *
         * @param literal the literal
         * @return the new resolved literal instance
         */
        public static Resolved fromObject(Literal literal, Datum resolvedFrom) {
            return new Resolved(literal.getValue().orElse(null), resolvedFrom);
        }

        @Override
        public Optional<Datum> getResolvedFrom() {
            return resolvedFrom;
        }
    }
}
