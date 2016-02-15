package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.data.container.UnresolvableContainer;
import com.pqqqqq.directscript.lang.exception.FailedExecutionException;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Compartment;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Kevin on 2015-06-12.
 * The context class combines a {@link Line} with its {@link ScriptInstance} to create {@link Statement.Argument} {@link Literal}
 */
public class Context {
    private final ScriptInstance scriptInstance;
    private final Line line;
    private final Line.Content content;

    private final String[] stringArguments;
    private final Statement.Arguments argumentsSet;
    private final Compartment compartment;
    private final Map<String, DataContainer> containers;
    private final Map<String, Literal> literals;

    private Statement.Result result = null;

    Context(ScriptInstance scriptInstance, Line line, List<Line.ContextPossibility> contextPossibilities) {
        this(scriptInstance, line, line.getContent(), contextPossibilities);
    }

    Context(ScriptInstance scriptInstance, Line line, Line.Content content, List<Line.ContextPossibility> contextPossibilities) {
        this.scriptInstance = scriptInstance;
        this.line = line;
        this.content = content;

        mainLoop:
        for (Line.ContextPossibility contextPossibility : contextPossibilities) {
            String[] stringArguments = contextPossibility.getStrargs();
            Statement.Arguments argumentSet = contextPossibility.getArgumentSet();
            Compartment compartment = contextPossibility.getCompartment();
            Map<Statement.Argument, DataContainer> containers = contextPossibility.getContainers();
            Map<String, DataContainer> newContainers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); // Case insensitive
            Map<String, Literal> literals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); // Case insensitive

            for (Map.Entry<Statement.Argument, DataContainer> entry : containers.entrySet()) {
                Literal literal = (entry.getValue() == null ? Literal.Literals.EMPTY : entry.getValue().resolve(this));

                Optional<Literal.Types> requiredType = entry.getKey().getRequiredType();
                if (requiredType.isPresent() && !requiredType.get().isCompatible(literal, false)) {
                    continue mainLoop;
                }

                if (entry.getKey().isMainGetter()) {
                    if (!compartment.containsGetter(literal.getString())) {
                        continue mainLoop;
                    }
                }

                newContainers.put(entry.getKey().getName(), entry.getValue());
                literals.put(entry.getKey().getName(), literal);
            }

            if (newContainers.size() != literals.size()) {
                continue;
            }

            this.stringArguments = stringArguments;
            this.argumentsSet = argumentSet;
            this.containers = newContainers;
            this.literals = literals;
            this.compartment = compartment;
            return;
        }

        throw new IllegalStateException("No Syntax can be inferred by this Context");
    }

    /**
     * Gets the {@link ScriptInstance} for this context
     *
     * @return the script instance
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * Gets the {@link Script} for this context. This is analogous to: <code>getScriptInstance().getScript()</code>
     *
     * @return the script
     */
    public Script getScript() {
        return scriptInstance.getScript();
    }

    /**
     * Gets the {@link Line} for this context
     *
     * @return the line
     */
    public Line getLine() {
        return line;
    }

    /**
     * Gets the {@link Line.Content} for this context
     * @return the content
     */
    public Line.Content getContent() {
        return content;
    }

    /**
     * Gets the string argument array
     *
     * @return the string arguments
     */
    public String[] getStringArguments() {
        return stringArguments;
    }

    /**
     * Gets the string argument at the given index
     *
     * @param index the index
     * @return the string argument
     */
    public String getStringArgument(int index) {
        return stringArguments[index];
    }

    /**
     * Gets the {@link com.pqqqqq.directscript.lang.statement.Statement.Arguments Argument syntax} this context utilizes
     * @return the argument syntax
     */
    public Statement.Arguments getArgumentsSet() {
        return argumentsSet;
    }

    /**
     * Gets the {@link Compartment} to run
     * @return the compartment
     */
    public Compartment getCompartment() {
        return compartment;
    }

    /**
     * Gets the {@link DataContainer} with the given name
     *
     * @param name the name
     * @return the data argument, or null if none
     */
    public DataContainer getContainer(String name) {
        DataContainer container = this.containers.get(name);

        if (container != null && container instanceof UnresolvableContainer) {
            return ((UnresolvableContainer) container).getDataContainer(); // Get the actual container here
        }

        return container;
    }

    /**
     * Gets the {@link Literal} with the given name
     *
     * @param name the name
     * @return the literal argument, or {@link Literal.Literals#EMPTY}
     */
    public Literal getLiteral(String name) {
        Literal literal = this.literals.get(name);
        if (literal == null) {
            literal = Literal.Literals.EMPTY;
        }

        if (!literal.isEmpty()) {
            Statement.Argument argument = argumentsSet.getArgument(name);
            Class<?> classType = literal.getValue().get().getClass();
            Function<Object, Object> conversionFunction = argument.getConversionMap().get(classType);

            if (conversionFunction != null) {
                return Literal.fromObject(conversionFunction.apply(literal.getValue().get()));
            }
        }

        return literal;
    }

    /**
     * Gets the {@link Literal} with the given name and type
     *
     * @param name the name
     * @param type the type for defaults
     * @return the literal, or {@link Literal.Literals#EMPTY}
     */
    public Literal getLiteral(String name, Class<?> type) {
        Literal literal = getLiteral(name);

        if (!literal.isEmpty()) {
            return literal; // Don't just use or because no need to iterate through this all
        }

        return (type == null ? literal : literal.or(() -> scriptInstance.getEventVarWithType(type)));
    }

    /**
     * Gets the {@link Literal} with the given name, or a default value if empty
     *
     * @param name the name
     * @param def the default value
     * @return the literal argument, or the default value
     */
    public Literal getLiteral(String name, Object def) {
        return getLiteral(name).or(def);
    }

    /**
     * Gets the {@link Literal} with the given name, or a default value if empty
     *
     * @param name the name
     * @param def the default value
     * @return the literal argument, or the default literal
     */
    public Literal getLiteral(String name, Literal def) {
        return getLiteral(name).or(def);
    }

    /**
     * Gets the {@link Literal} with the given name, or a default value if empty
     *
     * @param name     the name
     * @param supplier the {@link Supplier} for the default
     * @return the literal argument, or the default value
     */
    public Literal getLiteral(String name, Supplier supplier) {
        return getLiteral(name).or(supplier.get());
    }

    /**
     * Gets the number of argument {@link Literal}s
     *
     * @return the size of the literal array
     */
    public int getLiteralCount() {
        return literals.size();
    }

    /**
     * Runs this {@link Context}
     *
     * @return the {@link Statement.Result}
     */
    public Statement.Result run() {
        Statement.Argument objectArgument = content.getStatement().getObjectArgument();
        if (objectArgument == null) {
            result = compartment.run(this, null);
        } else {
            Class clazz = objectArgument.getObjectClass().orElse(null);
            Optional arg = getLiteral(objectArgument.getName(), clazz).getAs(clazz);
            if (!arg.isPresent()) {
                result = Statement.Result.NO_OBJECT_PRESENT;
            } else {
                result = compartment.run(this, arg.get());
            }
        }

        if (line != null/* && scriptInstance.isRuntime()*/ && (result == null || !result.isSuccess())) { // We only want this at runtime
            throw new FailedExecutionException("Statement in script '%s' -> '%s' at line #%d (script line #%d) failed. Reason: %s", getScript().getScriptsFile().getStringRepresentation(), getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber(), result.getError().orElse("Unknown"));
        }

        return result;
    }

    /**
     * Returns the {@link ScriptInstance.Result} of the last run of this context
     *
     * @return the result, or null if not run yet
     */
    public Statement.Result getResult() {
        return result;
    }

    /**
     * A default entity that can run a {@link Context}
     */
    public interface Runnable<T> {

        /**
         * The run command
         *
         * @param ctx the context
         * @return the {@link Statement.Result} of the execution
         */
        Statement.Result<T> run(Context ctx);

        interface Argumentative<T, R> {
            /**
             * The run command
             *
             * @param ctx      the context
             * @param argument the argument
             * @return the {@link Statement.Result} of the execution
             */
            Statement.Result<R> run(Context ctx, T argument);
        }
    }
}
