package com.pqqqqq.directscript.lang.statement;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Predicate;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.LocateableSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-12.
 * The abstract implementation of statements
 */
public abstract class Statement<T> implements Context.Runnable<T>, Compartment<Object, T> {
    private final Set<Compartment> compartments = new HashSet<>();

    protected Statement() {
    }

    /**
     * <p>Retrieves the {@link com.pqqqqq.directscript.lang.statement.Statement.Syntax} for this compartment.</p>
     * <p>It is preferred to have a final static instance of this syntax available always, and not for it to be rebuilt at the call of this method.</p>
     * @return the syntax
     */
    public abstract Statement.Syntax getSyntax();

    /**
     * <p>Gets the {@link Argument} considered to be the object</p>
     * <p>Object arguments are required to contain the flag {@link Argument#MAIN_OBJECT}</p>
     *
     * @return the object argument
     */
    public Argument getObjectArgument() {
        return null;
    }

    @Override
    public Arguments[] getArgumentsArray() {
        return getSyntax().getArguments();
    }

    @Override
    public String[] getGetters() {
        return null;
    }

    @Override
    public Result<T> run(Context ctx) {
        return (Result<T>) Result.UNSPECIFIED_GETTER;
    }

    @Override
    public Result<T> run(Context ctx, Object arg) {
        return run(ctx);
    }

    protected final void register(Compartment compartment) {
        compartments.add(compartment);
    }

    protected final void inherit(Statement other) {
        Set<Compartment> compartments = other.getCompartments();
        for (Compartment compartment : compartments) {
            register(createCompartment(compartment, GenericArguments.from(this, compartment.getArgumentsArray())));
        }
    }

    protected final <A, O> void inherit(Statement other, Function<A, O> conversionFunction) {
        Set<Compartment> compartments = other.getCompartments();
        for (Compartment compartment : compartments) {
            register(createCompartment(compartment, conversionFunction, GenericArguments.from(this, compartment.getArgumentsArray())));
        }
    }

    protected final <A, R> Compartment<A, R> createCompartment(final String getter, final Context.Runnable.Argumentative<A, R> runnable, final Arguments... arguments) {
        return createCompartment(new String[]{getter}, runnable, arguments);
    }

    protected final <A, R> Compartment<A, R> createCompartment(final String[] getters, final Context.Runnable.Argumentative<A, R> runnable, Arguments... arguments) {
        final Arguments[] givenArgs = (arguments == null || arguments.length == 0 ? new Arguments[]{Arguments.empty()} : arguments);
        return new Compartment<A, R>() {
            @Override
            public Arguments[] getArgumentsArray() {
                return givenArgs;
            }

            @Override
            public String[] getGetters() {
                return getters;
            }

            @Override
            public Result<R> run(Context ctx, A argument) {
                return runnable.run(ctx, argument);
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(getGetters());
            }
        };
    }

    protected final <A, R> Compartment<A, R> createCompartment(final Compartment<A, R> template, final Arguments... arguments) {
        return new Compartment<A, R>() {

            @Override
            public Result<R> run(Context ctx, A argument) {
                return template.run(ctx, argument);
            }

            @Override
            public Arguments[] getArgumentsArray() {
                return arguments;
            }

            @Override
            public String[] getGetters() {
                return template.getGetters();
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(getGetters());
            }
        };
    }

    protected final <A, R, O> Compartment<A, R> createCompartment(final Compartment<O, R> template, final Function<A, O> conversionFunction, final Arguments... arguments) {
        return new Compartment<A, R>() {

            @Override
            public Result<R> run(Context ctx, A argument) {
                return template.run(ctx, conversionFunction.apply(argument));
            }

            @Override
            public Arguments[] getArgumentsArray() {
                return arguments;
            }

            @Override
            public String[] getGetters() {
                return template.getGetters();
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(getGetters());
            }
        };
    }

    /**
     * Gets the {@link Compartment} {@link Set}
     * @return the set
     */
    public final Set<Compartment> getCompartments() {
        return compartments;
    }

    /*@Override
    public final Result<T> run(Context ctx) {
        Literal getter = ctx.getGetterLiteral().getRight();
        if (getter.isEmpty()) {
            return run(ctx);
        }

        Pair<Argument, Literal> objectTuple = ctx.getObjectLiteral();
        Optional arg = objectTuple.getRight().getAs(objectTuple.getLeft().getObjectClass().orElse(null));
        String getterString = Utilities.removeNonAlphanumeric(getter.getString());

        if (objectTuple.getLeft() != null && !arg.isPresent()) {
            return (Result<T>) Result.NO_OBJECT_PRESENT;
        }

        for (Compartment compartment : compartments) {
            if (compartment.containsGetter(getterString)) {
                return compartment.run(ctx, arg.get());
            }
        }

        return (Result<T>) Result.UNKNOWN_COMPARTMENT;
    }*/

    /**
     * An enumeration of execution times, to be used by {@link Line} {@link com.google.common.base.Predicate}s when executing a {@link com.pqqqqq.directscript.lang.script.ScriptInstance}
     *
     * @see Syntax#getExecutionTime()
     */
    public enum ExecutionTime {
        /**
         * Represents that this {@link Statement} is to only be run at runtime
         */
        RUNTIME,

        /**
         * Represents that this {@link Statement} is to only be run when compiling
         */
        COMPILE,

        /**
         * Represents that this {@link Statement} is always to be run
         */
        ALWAYS
    }

    /**
     * <p>Denotes a class that represents a concept {@link Statement} that should work given the Sponge API, but is not yet implemented.</p>
     * <p>These statements will be skipped by: {@link Statements#getStatement(String)}.</p>
     */
    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public @interface Concept {
    }

    /**
     * An immutable class that denotes a {@link Statement}'s syntax
     */
    public static class Syntax {
        private final String[] identifiers;
        private final String prefix;
        private final String suffix;

        private final boolean doesUseBrackets;

        private final Optional<Predicate<String>> customPredicate;

        private final ExecutionTime executionTime;
        private final Arguments[] arguments;

        // Generated
        private final Pattern matchPattern;

        Syntax(String[] identifiers, String prefix, String suffix, boolean doesUseBrackets, ExecutionTime executionTime, Predicate<String> customPredicate, Arguments[] arguments) {
            this.identifiers = identifiers;
            this.prefix = prefix;
            this.suffix = suffix;
            this.doesUseBrackets = doesUseBrackets;
            this.executionTime = executionTime;
            this.customPredicate = Optional.ofNullable(customPredicate);
            this.arguments = arguments;

            if (!this.customPredicate.isPresent()) { // No need to generate this if it's never used
                this.matchPattern = genMatchPattern();
            } else {
                this.matchPattern = null;
            }
        }

        /**
         * Gets a new {@link Syntax.Builder} instance
         *
         * @return the builder
         */
        public static Syntax.Builder builder() {
            return new Builder();
        }

        /**
         * Gets the identifiers for this statement, one of which is required to precede the prefix
         *
         * @return the identifier strings
         */
        public String[] getIdentifiers() {
            return identifiers;
        }

        /**
         * Gets the prefix for this statement. A {@link Line} must start with this to be considered (excluding whitespace)
         *
         * @return the prefix
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Gets the suffix for this statement. A {@link Line} must end with this to be considered (excluding whitespace)
         *
         * @return the suffix
         */
        public String getSuffix() {
            return suffix;
        }

        /**
         * Gets if this statement only checks for arguments that are inside brackets '()'
         *
         * @return true if uses argument brackets
         */
        public boolean doesUseBrackets() {
            return doesUseBrackets;
        }

        /**
         * Gets the {@link com.pqqqqq.directscript.lang.statement.Statement.ExecutionTime} for this statement
         *
         * @return the execution time
         */
        public ExecutionTime getExecutionTime() {
            return executionTime;
        }

        /**
         * <p>Gets the {@link Optional} String {@link Predicate} for this syntax.</p>
         * <p>If this value is present, all other matching utilities will be ignored, and this used instead.</p>
         * @return the predicate
         */
        public Optional<Predicate<String>> getCustomPredicate() {
            return customPredicate;
        }

        /**
         * Gets the array of {@link Arguments} for this statement
         *
         * @return the argument syntaxes
         */
        public Arguments[] getArguments() {
            return arguments;
        }

        /**
         * Gets whether a given line is applicable to this statement.
         *
         * @param line the line
         * @return true if the line matches
         */
        public boolean matches(String line) {
            if (customPredicate.isPresent()) { // Check custom predicate first
                return customPredicate.get().apply(line);
            } else {
                return matchPattern.matcher(line).matches();
            }
        }

        private String genIdentifierPatternString() {
            if (getIdentifiers() == null || getIdentifiers().length == 0) {
                return "";
            }

            String identifierString = "(";

            for (String identifier : getIdentifiers()) {
                identifierString += "\\Q" + identifier + "\\E|";
            }

            return identifierString.substring(0, identifierString.length() - 1) + ")";
        }

        private String genPrefixPatternString() {
            return getPrefix().isEmpty() ? "" : "\\Q" + getPrefix() + "\\E";
        }

        private String genSuffixPatternString() {
            return getSuffix().isEmpty() ? "" : "\\Q" + getSuffix() + "\\E";
        }

        private Pattern genMatchPattern() {
            if (!doesUseBrackets()) {
                return Pattern.compile("^(\\s*?)" + genPrefixPatternString() + genIdentifierPatternString() + "(.*?)" + genSuffixPatternString() + "(\\s*?)$", Pattern.CASE_INSENSITIVE); // DS is case insensitive to statements
            } else {
                return Pattern.compile("^(\\s*?)" + genPrefixPatternString() + genIdentifierPatternString() + "(\\s*?)\\((.*?)\\)(\\s*?)" + genSuffixPatternString() + "(\\s*?)$", Pattern.CASE_INSENSITIVE); // DS is case insensitive to statements
            }
        }

        /**
         * <p>The {@link Statement.Syntax} builder class</p>
         * <p>Defaults:</p>
         * <ul>
         * <li>Identifiers: Empty
         * <li>Prefix: Empty
         * <li>Suffix: Empty
         * <li>Brackets: Yes
         * <li>Execution time: RUNTIME
         * <li>Custom regex: null
         * <li>Argument syntaxes: Empty (no arguments)
         * </ul>
         */
        public static class Builder {
            private List<String> identifiers = new ArrayList<String>();
            private String prefix = "";
            private String suffix = "";
            private boolean doesUseBrackets = true;
            private ExecutionTime executionTime = ExecutionTime.RUNTIME;
            private Predicate<String> customPredicate = null;
            private List<Arguments> arguments = new ArrayList<Arguments>();

            Builder() { // Default visibility
            }

            /**
             * Adds the array of identifiers to this {@link Builder}
             *
             * @param identifiers the new identifiers
             * @return this builder, for fluency
             * @see Syntax#getIdentifiers()
             */
            public Builder identifiers(String... identifiers) {
                this.identifiers.addAll(Arrays.asList(identifiers));
                return this;
            }

            /**
             * Sets the prefix for this {@link Builder}
             *
             * @param prefix the new prefix
             * @return this builder, for fluency
             * @see Syntax#getPrefix()
             */
            public Builder prefix(String prefix) {
                this.prefix = prefix;
                return this;
            }

            /**
             * Sets the suffix for this {@link Builder}
             *
             * @param suffix the new suffix
             * @return this builder, for fluency
             * @see Syntax#getSuffix()
             */
            public Builder suffix(String suffix) {
                this.suffix = suffix;
                return this;
            }

            /**
             * Sets whether this {@link Builder} should use brackets
             *
             * @param doesUseBrackets the new brackets state
             * @return this builder, for fluency
             * @see Syntax#doesUseBrackets
             */
            public Builder brackets(boolean doesUseBrackets) {
                this.doesUseBrackets = doesUseBrackets;
                return this;
            }

            /**
             * Toggles whether this {@link Builder} should use brackets
             *
             * @return this builder, for fluency
             */
            public Builder brackets() {
                return brackets(!doesUseBrackets);
            }

            /**
             * Sets the {@link Statement.ExecutionTime} for this {@link Builder}
             *
             * @param executionTime the new execution time
             * @return this builder, for fluency
             * @see Syntax#getExecutionTime()
             */
            public Builder executionTime(ExecutionTime executionTime) {
                this.executionTime = executionTime;
                return this;
            }

            /**
             * Sets the custom {@link Predicate} for this {@link Builder}
             * @param customPredicate the new custom predicate
             * @return this builder, for fluency
             * @see Syntax#getCustomPredicate()
             */
            public Builder customPredicate(Predicate<String> customPredicate) {
                this.customPredicate = customPredicate;
                return this;
            }

            /**
             * Adds the array of {@link Arguments}es to this {@link Builder}
             *
             * @param arguments the new argument syntaxes
             * @return this builder, for fluency
             * @see Syntax#getArguments()
             */
            public Builder arguments(Arguments... arguments) {
                this.arguments.addAll(Arrays.asList(arguments));
                return this;
            }

            /**
             * Builds the {@link Syntax} instance
             *
             * @return the new syntax instance
             */
            public Syntax build() {
                if (this.arguments.isEmpty()) {
                    this.arguments.add(Arguments.empty());
                }

                Collections.sort(this.arguments);
                return new Syntax(identifiers.toArray(new String[identifiers.size()]), prefix, suffix, doesUseBrackets, executionTime, customPredicate, arguments.toArray(new Arguments[arguments.size()]));
            }
        }
    }

    /**
     * The {@link Argument} sequence for this {@link Statement}
     */
    public static class Arguments implements Comparable<Arguments> {
        private static final Arguments EMPTY = new Arguments(new Argument[0], new String[0]);
        private final Argument[] arguments;
        private final String[] delimiters;

        Arguments(Argument[] arguments, String[] delimiters) {
            this.arguments = arguments;
            this.delimiters = delimiters;
        }

        /**
         * Returns an empty {@link Arguments} (no {@link Statement.Argument}s)
         * @return the empty argument sequence
         */
        public static Arguments empty() {
            return EMPTY;
        }

        /**
         * <p>Creates a new {@link Arguments} instance with the given Object sequence</p>
         * <p>Accepted types: Argument and String</p>
         * @param sequence the sequence vararg
         * @return the new instance
         */
        public static Arguments of(Object... sequence) {
            Argument[] arguments = new Argument[(int) Math.ceil(checkNotNull(sequence, "Sequence cannot be null").length / 2D)];
            String[] delimiters = new String[(int) Math.floor(sequence.length / 2D)];

            Class<?> lastType = null;
            int argumentIndex = 0, delimiterIndex = 0;

            for (Object obj : sequence) {
                checkState(obj instanceof Argument || obj instanceof String, "Unknown type in argument syntax: " + obj.getClass().getName());
                checkState(lastType == null || !lastType.equals(obj.getClass()), "Do not repeat two types after each other");
                lastType = obj.getClass();

                if (obj instanceof Argument) {
                    arguments[argumentIndex++] = (Argument) obj;
                } else if (obj instanceof String) {
                    delimiters[delimiterIndex++] = (String) obj;
                }
            }
            return new Arguments(arguments, delimiters);
        }

        /**
         * Gets a new arguments instance using a template arguments, and switching only the main object argument
         *
         * @param statement the new statement
         * @param template  the template arguments
         * @return the new arguments
         */
        public static Arguments from(Statement statement, Arguments template) {
            Argument[] arguments = new Argument[template.getArguments().length];
            for (int i = 0; i < arguments.length; i++) {
                Argument argumentTemplate = template.getArguments()[i];
                if (argumentTemplate.isMainObject()) {
                    arguments[i] = statement.getObjectArgument(); // New object argument
                } else {
                    arguments[i] = argumentTemplate; // Add normally
                }
            }

            return new Arguments(arguments, template.getDelimiters()); // Delimiters don't change
        }

        /**
         * Gets the {@link Statement.Argument} array sequence
         * @return the argument sequence
         */
        public Argument[] getArguments() {
            return arguments;
        }

        /**
         * Gets an argument by name
         * @param name the name
         * @return the argument, or null if none
         */
        public Argument getArgument(String name) {
            for (Argument argument : arguments) {
                if (argument.getName().equalsIgnoreCase(name)) {
                    return argument;
                }
            }

            return null;
        }

        /**
         * Gets the string delimiters array sequence
         *
         * @return the delimiter sequence
         */
        public String[] getDelimiters() {
            return delimiters;
        }

        @Override
        public int compareTo(Arguments o) {
            if (arguments.length > o.getArguments().length || delimiters.length > o.getDelimiters().length) {
                return -1;
            }

            if (arguments.length < o.getArguments().length || delimiters.length < o.getDelimiters().length) {
                return 1;
            }

            return 0;
        }

        @Override
        public String toString() {
            return Arrays.toString(arguments) + " :: " + Arrays.toString(delimiters);
        }
    }

    /**
     * Created by Kevin on 2015-06-12.
     * Represents an immutable argument for a {@link Statement} that has different properties
     */
    public static class Argument {
        /**
         * Denotes an argument that does not parse
         */
        public static final int NO_PARSE = 0x01;

        /**
         * Denotes an argument that does not resolve (this must be present if the {@link #NO_PARSE} flag is present)
         */
        public static final int NO_RESOLVE = 0x02;

        /**
         * Denotes an argument which creates a variable if none are found
         */
        public static final int CREATE_VARIABLE_IF_NONE = 0x04;

        /**
         * Denotes an argument that takes the rest of the provided arguments as a list
         */
        public static final int REST_AS_LIST = 0x08;

        /**
         * Denotes an argument that is the main getter
         */
        public static int MAIN_GETTER = 0x10;

        /**
         * Denotes an argument that is the main object
         */
        public static int MAIN_OBJECT = 0x20;

        private final String name;
        private final int flags;
        private final Optional<Literal.Types> requiredType;
        private final Optional<Class> objectClass;
        private final Map<Class, Function<Object, Object>> conversionMap;

        Argument(String name, int flags, Literal.Types requiredType, Class objectClass, Map<Class, Function<Object, Object>> conversionMap) {
            this.name = name;
            this.flags = flags;
            this.requiredType = Optional.ofNullable(requiredType);
            this.objectClass = Optional.ofNullable(objectClass);
            this.conversionMap = conversionMap;
        }

        /**
         * Creates a new Argument {@link Builder}
         * @return the new builder instance
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Gets the name of this argument
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets if this argument should be parsed by {@link com.pqqqqq.directscript.lang.data.Sequencer#parse(String)}
         *
         * @return whether to parse this argument
         */
        public boolean doParse() {
            return !has(NO_PARSE);
        }

        /**
         * Gets if this argument should be resolved by {@link com.pqqqqq.directscript.lang.data.container.DataContainer#resolve(Context)}
         * @return true if the argument should be resolved
         */
        public boolean doResolve() {
            return !has(NO_RESOLVE);
        }

        /**
         * Gets if this argument should create a new variable if none exist
         * @return true if the argument should create a new variable by default
         */
        public boolean doCreateVariable() {
            return has(CREATE_VARIABLE_IF_NONE);
        }

        /**
         * Gets if this argument should conjugate trailing arguments into a list
         *
         * @return true if conjugation should occur
         */
        public boolean doConjugateToList() {
            return has(REST_AS_LIST);
        }

        /**
         * Gets if this argument is the main getter
         * @return true if main getter
         */
        public boolean isMainGetter() {
            return has(MAIN_GETTER);
        }

        /**
         * Gets if this argument is the main object
         *
         * @return true if main object
         */
        public boolean isMainObject() {
            return has(MAIN_OBJECT);
        }

        /**
         * Gets the required {@link com.pqqqqq.directscript.lang.data.Literal.Types Literal Type} for this argument to persist
         *
         * @return the required type
         */
        public Optional<Literal.Types> getRequiredType() {
            return requiredType;
        }

        public Optional<Class> getObjectClass() {
            return objectClass;
        }

        public Map<Class, Function<Object, Object>> getConversionMap() {
            return conversionMap;
        }

        private boolean has(int flag) {
            return (flags & flag) != 0;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * The {@link Argument} builder
         */
        public static class Builder {
            private String name = null;
            private int flags = 0;
            private Literal.Types requiredType = null;
            private Class objectClass = null;
            private Map<Class, Function<Object, Object>> conversionMap = new HashMap<>();

            private Builder() {
            }

            /**
             * Sets the name of this argument builder
             *
             * @param name the new name
             * @return this builder, for chaining
             */
            public Builder name(String name) {
                this.name = name;
                return this;
            }

            /**
             * Adds a flag to this argument builder
             *
             * @param flags the flags to add
             * @return this builder, for chaining
             */
            public Builder addFlags(int... flags) {
                for (int flag : flags) {
                    this.flags |= flag;
                }
                return this;
            }

            /**
             * Sets the collective flags for this argument builder
             *
             * @param flags the new flags
             * @return this builder, for chaining
             */
            public Builder setFlags(int flags) {
                this.flags = flags;
                return this;
            }

            /**
             * Sets the required {@link com.pqqqqq.directscript.lang.data.Literal.Types} for this argument builder
             *
             * @param requiredType the required type
             * @return this builder, for chaining
             */
            public Builder requiredType(Literal.Types requiredType) {
                this.requiredType = requiredType;
                return this;
            }

            /**
             * Sets the object class
             *
             * @param objectClass the object class
             * @return this builder, for chaining
             */
            public Builder objectClass(Class objectClass) {
                this.objectClass = objectClass;
                return this;
            }

            /**
             * Adds a conversion {@link Function} entry
             *
             * @param convertedClass     the input class of the function
             * @param conversionFunction the conversion function
             * @return this builder, for chaining
             */
            public <T> Builder addConversion(Class<T> convertedClass, Function<T, Object> conversionFunction) {
                this.conversionMap.put(convertedClass, (Function<Object, Object>) conversionFunction);
                return this;
            }

            /**
             * Builds the new {@link Argument}
             *
             * @return the new argument instance
             */
            public Argument build() {
                return new Argument(checkNotNull(name, "Argument name must be specified."), flags, requiredType, objectClass, conversionMap);
            }
        }
    }

    /**
     * A class pertaining to a list of generic {@link Argument}s
     */
    public static class GenericArguments {
        /**
         * A getter argument with the key "Getter" and who has a string required type
         */
        public static final Argument GETTER = withNameFlagsAndType("Getter", Argument.MAIN_GETTER, Literal.Types.STRING);

        /**
         * A generic Object argument with the key "Object"
         */
        public static final Argument OBJECT = withName("Object");

        /**
         * A generic arguments argument with the key "Arguments" which uses conjugates trailing arguments (varargs) and requires an array type.
         */
        public static final Argument ARGUMENTS = withNameFlagsAndType("Arguments", Argument.REST_AS_LIST, Literal.Types.ARRAY);
        // Conversions
        public static final Argument DEFAULT_PLAYER = player("Player", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_ENTITY = entity("Entity", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_LIVING = living("Living", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_WORLD = world("World", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_LOCATION = location("Location", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_POSITION = position("Position", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_BLOCK_POSITION = blockPosition("BlockPosition", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_ROTATION = rotation("Rotation", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_VELOCITY = velocity("Velocity", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_VECTOR = vector("Vector", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_BLOCK = block("Block", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_TRANSFORM = transform("Transform", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_EXPLOSION = explosion("Explosion", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_ITEM_STACK = itemStack("ItemStack", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_TRANSACTION = transaction("Transaction", Argument.MAIN_OBJECT, null);
        public static final Argument DEFAULT_SOURCE = transaction("Source", Argument.MAIN_OBJECT, null);

        public static Arguments[] getterArguments(Argument objectArgument) {
            if (objectArgument == null) {
                return new Arguments[]{Arguments.of(GETTER)};
            } else {
                return new Arguments[]{Arguments.of(GETTER), Arguments.of(objectArgument, ",", GETTER)};
            }
        }

        public static Arguments[] getterArguments(Statement statement) {
            return getterArguments(statement.getObjectArgument());
        }

        public static Arguments[] requiredArguments(Argument objectArgument, Argument[] required, Argument... unrequired) {
            List<Arguments> argumentsList = new ArrayList<>();

            Object[] object = new Object[required.length * 2 + 3 + unrequired.length * 2]; // Object, getter and required arguments
            Object[] noObject = new Object[required.length * 2 + 1 + unrequired.length * 2]; // Getter and required arguments

            object[0] = objectArgument;
            object[1] = ",";
            object[2] = noObject[0] = GETTER;

            int currentObjectIndex = 3, currentNoObjectIndex = 1;
            for (Argument req : required) { // First add required arguments
                object[currentObjectIndex++] = noObject[currentNoObjectIndex++] = ",";
                object[currentObjectIndex++] = noObject[currentNoObjectIndex++] = req;
            }

            BiConsumer<Integer, Integer> run = (curObjectIndex, curNoObjectIndex) -> {
                Object[] currentObject = new Object[curObjectIndex]; // Don't add one to size because one is added already
                Object[] currentNoObject = new Object[curNoObjectIndex];

                for (int i = 0; i < currentObject.length; i++) {
                    currentObject[i] = object[i];
                    if (i <= (currentNoObject.length - 1)) {
                        currentNoObject[i] = noObject[i];
                    }
                }

                if (objectArgument != null) {
                    argumentsList.add(Arguments.of(currentObject)); // Final object
                }

                argumentsList.add(Arguments.of(currentNoObject)); // Final no object
            };

            for (Argument arg : unrequired) {
                run.accept(currentObjectIndex, currentNoObjectIndex);
                object[currentObjectIndex++] = noObject[currentNoObjectIndex++] = ","; // Add comma first
                object[currentObjectIndex++] = noObject[currentNoObjectIndex++] = arg;
            }
            run.accept(currentObjectIndex, currentNoObjectIndex); // Adds last arguments, and important for when no unrequired arguments

            return argumentsList.toArray(new Arguments[argumentsList.size()]);
        }

        public static Arguments[] requiredArguments(Statement statement, Argument[] required, Argument... unrequired) {
            return requiredArguments(statement.getObjectArgument(), required, unrequired);
        }

        public static Arguments[] requiredArguments(Argument objectArgument, Argument required, Argument... unrequired) {
            return requiredArguments(objectArgument, (required == null ? new Argument[0] : new Argument[]{required}), unrequired);
        }

        public static Arguments[] requiredArguments(Statement statement, Argument required, Argument... unrequired) {
            return requiredArguments(statement.getObjectArgument(), required, unrequired);
        }

        public static Arguments[] from(Statement newStatement, Arguments[] template) {
            Arguments[] arguments = new Arguments[template.length];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = Arguments.from(newStatement, template[i]);
            }

            return arguments;
        }

        /**
         * Creates an {@link Argument} with the given name
         *
         * @param name the name of the argument
         * @return the new argument instance
         */
        public static Argument withName(String name) {
            return Argument.builder().name(name).build();
        }

        /**
         * Creates an {@link Argument} with the given name and {@link com.pqqqqq.directscript.lang.data.Literal.Types}
         *
         * @param name         the name
         * @param requiredType the required type
         * @return the new argument instance
         */
        public static Argument withNameAndType(String name, Literal.Types requiredType) {
            return Argument.builder().name(name).requiredType(requiredType).build();
        }

        /**
         * Creates an {@link Argument} with the given name and flags
         *
         * @param name  the name
         * @param flags the flags, already bit-shifted
         * @return the new argument instance
         */
        public static Argument withNameAndFlags(String name, int flags) {
            return Argument.builder().name(name).setFlags(flags).build();
        }

        /**
         * Creates an {@link Argument} with the given name and flags
         *
         * @param name  the name
         * @param flags the flags
         * @return the new argument instance
         */
        public static Argument withNameAndFlags(String name, int... flags) {
            return Argument.builder().name(name).addFlags(flags).build();
        }

        /**
         * Creates an {@link Argument} with the given name, flags and {@link com.pqqqqq.directscript.lang.data.Literal.Types}
         *
         * @param name         the name
         * @param flags        the flags, already bit-shifted
         * @param requiredType the required type
         * @return the new argument instance
         */
        public static Argument withNameFlagsAndType(String name, int flags, Literal.Types requiredType) {
            return Argument.builder().name(name).setFlags(flags).requiredType(requiredType).build();
        }

        /**
         * Creates an {@link Argument} with the given name, flags and {@link com.pqqqqq.directscript.lang.data.Literal.Types}
         *
         * @param name         the name
         * @param flags        the flags
         * @param requiredType the required type
         * @return the new argument instance
         */
        public static Argument withNameFlagsAndType(String name, int[] flags, Literal.Types requiredType) {
            return Argument.builder().name(name).addFlags(flags).requiredType(requiredType).build();
        }

        public static Argument player(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Player.class)
                    .addConversion(String.class, (string) -> DirectScript.instance().getGame().getServer().getPlayer(string).orElse(null))
                    .build();
        }

        public static Argument entity(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Entity.class)
                    // TODO UUID
                    .build();
        }

        public static Argument living(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Living.class)
                    // TODO UUID
                    .build();
        }

        public static Argument world(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(World.class)
                    .addConversion(String.class, (string) -> DirectScript.instance().getGame().getServer().getWorld(string).orElse(null))
                    .addConversion(Entity.class, (entity) -> entity.getWorld())
                    .addConversion(Location.class, (location) -> location.getExtent())
                    .addConversion(Transform.class, (transform) -> transform.getExtent())
                    .addConversion(LocateableSnapshot.class, (snapshot) -> ((Location<World>) snapshot.getLocation().get()).getExtent())
                    .build();
        }

        public static Argument location(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Location.class)
                    .addConversion(Entity.class, (entity) -> entity.getLocation())
                    .addConversion(Transform.class, (transform) -> transform.getLocation())
                    .addConversion(LocateableSnapshot.class, (snapshot) -> snapshot.getLocation().get())
                    .build();
        }

        public static Argument position(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Vector3d.class)
                    .addConversion(Entity.class, (entity) -> entity.getLocation().getPosition())
                    .addConversion(Location.class, (location) -> location.getPosition())
                    .addConversion(Transform.class, (transform) -> transform.getPosition())
                    .addConversion(LocateableSnapshot.class, (snapshot) -> ((Location) snapshot.getLocation().get()).getPosition())
                    .build();
        }

        public static Argument blockPosition(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Vector3i.class)
                    .addConversion(Entity.class, (entity) -> entity.getLocation().getBlockPosition())
                    .addConversion(Location.class, (location) -> location.getBlockPosition())
                    .addConversion(Transform.class, (transform) -> transform.getLocation().getBlockPosition())
                    .addConversion(LocateableSnapshot.class, (snapshot) -> ((Location) snapshot.getLocation().get()).getBlockPosition())
                    .build();
        }

        public static Argument rotation(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Vector3d.class)
                    .addConversion(Entity.class, (entity) -> entity.getRotation())
                    .build();
        }

        public static Argument velocity(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Vector3d.class)
                    .addConversion(Entity.class, (entity) -> entity.get(Keys.VELOCITY).orElse(null))
                    .build();
        }

        public static Argument vector(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Vector3d.class)
                    .addConversion(Entity.class, (entity) -> entity.get(Keys.VELOCITY).orElse(null))
                    .build();
        }

        public static Argument block(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(BlockSnapshot.class)
                    .addConversion(Entity.class, (entity) -> entity.getLocation().createSnapshot())
                    .addConversion(Location.class, (location) -> location.createSnapshot())
                    .addConversion(Transform.class, (transform) -> transform.getLocation().createSnapshot())
                    .build();
        }

        public static Argument transform(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Transform.class)
                    .addConversion(Entity.class, (entity) -> entity.getTransform())
                    .build();
        }

        public static Argument explosion(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Explosion.class)
                    .build();
        }

        public static Argument itemStack(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(ItemStack.class)
                    .build();
        }

        public static Argument transaction(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(Transaction.class)
                    .build();
        }

        public static Argument source(String argumentName, int flags, Literal.Types requiredType) {
            return Argument.builder().name(argumentName).setFlags(flags).requiredType(requiredType).objectClass(CommandSource.class)
                    .build();
        }
    }

    /**
     * Created by Kevin on 2015-06-02.
     * Represents the immutable result of executing a {@link Context} by {@link Context#run()}
     */
     public static class Result<T> {
        public static final Result<Object> NO_OBJECT_PRESENT = builder().failure().error("No object specified").build();
        private static final Result<Object> SUCCESS = builder().success().build();
        private static final Result<Object> FAILURE = builder().failure().build();
        private static final Result<?> UNSPECIFIED_GETTER = builder().failure().error("You must specify a getter").build();

        private final Optional<T> result;
        private final Optional<String> error;
        private final boolean success;

        Result(T result, String error, boolean success) {
            this.result = Optional.ofNullable(result);
            this.error = Optional.ofNullable(error);
            this.success = success;
        }

        /**
         * Gets a new {@link Builder} instance
         *
         * @param <T> the type for the builder/result
         * @return the new builder instance
         */
        public static <T> Builder<T> builder() {
            return new Builder<>();
        }

        /**
         * Gets a success result that is cast to a generic type
         *
         * @param <T> the generic type for this result
         * @return the result
         */
        public static <T> Result<T> success() {
            return (Result<T>) SUCCESS;
        }

        /**
         * Gets a failure result that is cast to a generic type
         *
         * @param <T> the generic type for this result
         * @return the result
         */
        public static <T> Result<T> failure() {
            return (Result<T>) FAILURE;
        }

        /**
         * Gets the {@link Optional} result of this {@link Result}
         *
         * @return the result
         */
        public Optional<T> getResult() {
            return result;
        }

        /**
         * Gets the {@link Optional} error message for this result
         * @return the error message, or {@link Optional#empty()}
         */
        public Optional<String> getError() {
            return error;
        }

        /**
         * Gets the {@link Optional} {@link Literal} result of this {@link Result}
         *
         * @return the literal result
         */
        public <T> Optional<Literal<T>> getLiteralResult() {
            if (getResult().isPresent()) {
                if (getResult().get() instanceof Literal) {
                    return Optional.of((Literal) getResult().get());
                }

                return Optional.of(Literal.fromObject(getResult().get()));
            }
            return Optional.empty();
        }

        /**
         * Gets if this {@link Result} represents a successul result
         *
         * @return true if successful
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * The builder for building {@link Result}s
         *
         * @param <T> the type to cast the result to
         */
        public static class Builder<T> {
            private T result = null;
            private String error = null;
            private Boolean success = null;

            Builder() { // Default view
            }

            /**
             * Sets the result of this {@link Result}
             *
             * @param result the new result
             * @return this builder, for fluency
             * @see Result#getResult()
             */
            public Builder<T> result(T result) {
                this.result = result;
                return this;
            }

            /**
             * Sets the error message for this result builder
             * @param error the new error message
             * @return this builder, for chaining
             * @see Result#getError()
             */
            public Builder<T> error(String error) {
                this.error = error;
                return this;
            }

            /**
             * Sets the success value of this {@link Result}
             *
             * @param success the success value
             * @return this builder, for fluency
             * @see Result#isSuccess()
             */
            public Builder<T> success(boolean success) {
                this.success = success;
                return this;
            }

            /**
             * Sets the success value of this {@link Result} to true
             *
             * @return this builder, for fluency
             * @see Result#isSuccess()
             */
            public Builder<T> success() {
                this.success = true;
                return this;
            }

            /**
             * Sets the success value of this {@link Result} to false
             *
             * @return this builder, for fluency
             * @see Result#isSuccess()
             */
            public Builder<T> failure() {
                this.success = false;
                return this;
            }

            /**
             * Builds the current data into a new {@link Result} instance
             *
             * @return the new result instance
             */
            public Result<T> build() {
                return new Result<T>(result, error, checkNotNull(success, "Success"));
            }
        }
    }
}
