package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Kevin on 2015-06-12.
 * The context class combines a {@link Line} with its {@link ScriptInstance} to create {@link Statement.Argument} {@link Literal}s
 */
public class Context {
    private final ScriptInstance scriptInstance;
    private final Line line;

    private final String[] stringArguments;
    private final Map<String, DataContainer> containers;
    private final Map<String, Literal> literals;

    private Statement.Result result = null;

    Context(ScriptInstance scriptInstance, Line line, String[] stringArguments, Map<String, DataContainer> containers) {
        this.scriptInstance = scriptInstance;
        this.line = line;

        this.stringArguments = stringArguments;
        this.containers = containers;

        this.literals = new TreeMap<String, Literal>(String.CASE_INSENSITIVE_ORDER); // Case insensitive
        for (Map.Entry<String, DataContainer> entry : containers.entrySet()) {
            this.literals.put(entry.getKey(), (entry.getValue() == null ? Literal.Literals.EMPTY : entry.getValue().resolve(scriptInstance)));
        }
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
     * Gets the {@link DataContainer} with the given name
     *
     * @param name the name
     * @return the data argument, or null if none
     */
    public DataContainer getContainer(String name) {
        return this.containers.get(name);
    }

    /**
     * Gets the {@link Literal} with the given name
     *
     * @param name the name
     * @return the literal argument, or {@link Literal.Literals#EMPTY}
     */
    public Literal getLiteral(String name) {
        Literal literal = this.literals.get(name);
        return (literal == null ? Literal.Literals.EMPTY : literal).or(this.scriptInstance.getEventVars().get(name));
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
        result = line.getStatement().run(this);
        if (scriptInstance.isRuntime() && result != null && !result.isSuccess()) { // We only want this at runtime
            Lang.instance().errorHandler().log(String.format("Statement in script '%s' -> '%s' at line #%d (script line #%d) failed. Continuing execution.", getScript().getScriptsFile().getStringRepresentation(), getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber()));
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
