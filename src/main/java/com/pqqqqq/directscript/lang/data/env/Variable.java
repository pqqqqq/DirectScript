package com.pqqqqq.directscript.lang.data.env;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.mutable.DataHolder;
import com.pqqqqq.directscript.lang.exception.IncompatibleTypeException;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a memory section that contains a {@link Literal} and is read by a specific name
 */
public class Variable<C extends Datum<?>> extends DataHolder<C> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]([A-Za-z0-9]|\\.|\\_|\\:)*$");
    private static final Pattern ILLEGAL_NAMES = Pattern.compile("/^(local|global|public|final|parse|in|and|or)$/");

    private final String name;
    private final Environment environment;

    private Optional<Literal.Types> typesOptional;

    /**
     * Creates a new variable with the corresponding name that has a value of {@link Literal.Literals#EMPTY}
     *
     * @param name the name
     * @param environment the environment
     */
    public Variable(String name, Environment environment) {
        this(name, environment, (C) Literal.Literals.EMPTY);
    }

    /**
     * Creates a new variable with the corresponding name and {@link Datum}
     *
     * @param name the name
     * @param environment the environment
     * @param dataContainer the data container
     */
    public Variable(String name, Environment environment, C dataContainer) {
        this(name, environment, dataContainer, Optional.empty());
    }

    /**
     * Creates a new variable with the corresponding name and {@link Datum}, and the given type
     *
     * @param name the name
     * @param environment the environment
     * @param dataContainer the data container
     */
    public Variable(String name, Environment environment, C dataContainer, Optional<Literal.Types> type) {
        super();
        this.name = checkNotNull(name, "Name cannot be null.");
        this.environment = checkNotNull(environment, "Environment cannot be null.");
        this.typesOptional = checkNotNull(type, "Type optional cannot be null");

        // Type set before data
        this.forceData(dataContainer);
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

    /**
     * <p>Gets the explicit {@link Literal.Types type} of this variable.</p>
     * <p>Types in variables are used for explicit type casting, and are therefore not necessary ({@link Optional})</p>
     * <p>Variable types can be changed as its set, and is not final.</p>
     * <p>If the variable's type is not ambiguous (is present), its value must match its corresponding type, according to {@link Literal.Types#isCompatible(Datum)}</p>
     *
     * @return the variable's type
     */
    public Optional<Literal.Types> getType() {
        return typesOptional;
    }

    @Override
    public void setDatum(C datum) {
        boolean unequal = !checkNotNull(datum, "Data itself cannot be null. Use Literal#empty for null data").equals(getDatum());

        try {
            forceData(datum);
        } finally {
            if (unequal && !this.environment.suppressNotifications) {
                this.environment.notifyChange();
            }
        }
    }

    private void forceData(C datum) { // Used in constructor
        // Ensure type consistency
        if (getType().isPresent()) {
            Literal.Types type = getType().get();

            if (!type.isCompatible(datum)) {
                throw new IncompatibleTypeException("The variable '%s', with the %s type is not compatible with the value: %s.", getName(), type.getName(), datum.toString());
            } else {
                // If it's empty, give it its respective empty type
                Literal getOrNull = datum.tryLiteral();
                if (getOrNull != null && getOrNull.isEmpty()) {
                    datum = (C) type.getEmpty();
                }
            }
        }

        super.setDatum(datum);
    }

    /**
     * Saves the variable to the config
     * @param node the root node for the variable
     */
    public void save(CommentedConfigurationNode node) {
        node.getNode("type").setValue(getType().isPresent() ? getType().get().getName() : "null");
        node.getNode("value").setValue(getDatum().serialize());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("environment", environment)
                .add("data", getDatum()).toString();
    }

    @Override
    public int hashCode() {
        return name.hashCode(); // We just want the natural object hashCode here, since Environments' HashMap requires a static hashCode
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Variable) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }
}
