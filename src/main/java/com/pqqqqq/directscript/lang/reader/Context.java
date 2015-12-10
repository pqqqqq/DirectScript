package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.data.container.UnresolvableContainer;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Created by Kevin on 2015-06-12.
 * The context class combines a {@link Line} with its {@link ScriptInstance} to create {@link Statement.Argument} {@link Datum}
 */
public class Context {
    private final ScriptInstance scriptInstance;
    private final Line line;

    private final String[] stringArguments;
    private final Statement.Arguments argumentsSet;
    private final Map<String, DataContainer> containers;
    private final Map<String, Datum> data;

    private Statement.Result result = null;

    Context(ScriptInstance scriptInstance, Line line, List<Line.ContextPossibility> contextPossibilities) {
        this.scriptInstance = scriptInstance;
        this.line = line;

        mainLoop:
        for (Line.ContextPossibility contextPossibility : contextPossibilities) {
            String[] stringArguments = contextPossibility.getStrargs();
            Statement.Arguments argumentSet = contextPossibility.getArgumentSet();
            Map<Statement.Argument, DataContainer> containers = contextPossibility.getContainers();
            Map<String, DataContainer> newContainers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); // Case insensitive
            Map<String, Datum> data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER); // Case insensitive

            for (Map.Entry<Statement.Argument, DataContainer> entry : containers.entrySet()) {
                Datum datum = (entry.getValue() == null ? Literal.Literals.EMPTY : entry.getValue().resolve(this));

                Optional<Literal.Types> requiredType = entry.getKey().getRequiredType();
                if (requiredType.isPresent() && !requiredType.get().isCompatible(datum)) {
                    continue mainLoop;
                }

                newContainers.put(entry.getKey().getName(), entry.getValue());
                data.put(entry.getKey().getName(), datum);
            }

            if (newContainers.size() != data.size()) {
                continue;
            }

            this.stringArguments = stringArguments;
            this.argumentsSet = argumentSet;
            this.containers = newContainers;
            this.data = data;
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
     * Gets the {@link Datum} with the given name
     * @param name the name
     * @return the data argument, or {@link Literal.Literals#EMPTY}
     */
    public Datum getDatum(String name) {
        Datum data = this.data.get(name);
        return data == null ? Literal.Literals.EMPTY : data;
    }

    /**
     * Gets the {@link Literal} with the given name
     *
     * @param name the name
     * @return the literal argument, or {@link Literal.Literals#EMPTY}
     */
    public Literal getLiteral(String name) {
        return getDatum(name).get();
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

        return literal.or(scriptInstance.getEventVarWithType(type));
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
        return data.size();
    }

    /**
     * Runs this {@link Context}
     *
     * @return the {@link Statement.Result}
     */
    public Statement.Result run() {
        result = line.getStatement().run(this);
        if (scriptInstance.isRuntime() && result != null && !result.isSuccess()) { // We only want this at runtime
            Lang.instance().errorHandler().log(String.format("Statement in script '%s' -> '%s' at line #%d (script line #%d) failed. Reason: %s. Continuing execution.", getScript().getScriptsFile().getStringRepresentation(), getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber(), result.getResult().orElse("")));
            Lang.instance().errorHandler().flush();
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
}
