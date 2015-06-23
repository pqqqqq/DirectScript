package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Literals;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;

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

        this.literals = new HashMap<String, Literal>();
        for (Map.Entry<String, DataContainer> entry : containers.entrySet()) {
            this.literals.put(entry.getKey(), (entry.getValue() == null ? Literals.EMPTY : entry.getValue().resolve(scriptInstance)));
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
     * @return the literal argument, or {@link Literals#EMPTY}
     */
    public Literal getLiteral(String name) {
        Literal literal = this.literals.get(name);
        return literal == null ? Literals.EMPTY : literal;
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
     * Returns the {@link com.pqqqqq.directscript.lang.script.ScriptInstance.Result} of the last run of this context
     *
     * @return the result, or null if not run yet
     */
    public Statement.Result getResult() {
        return result;
    }

    // Convenience stuff

    /**
     * Gets an {@link Optional} {@link Player} at the index that, if {@link com.pqqqqq.directscript.lang.data.Literals#EMPTY}, uses the {@link ScriptInstance#getCausedBy()} player instead
     *
     * @param name the name
     * @return the player
     */
    public Optional<Player> getPlayerOrCauser(String name) {
        Optional<Player> causedBy = this.scriptInstance.getCausedBy();
        Literal literal = getLiteral(name);
        return (literal.isEmpty() ? causedBy : literal.getPlayer());
    }

    /**
     * Gets an {@link Optional} {@link World} at the index that, if {@link com.pqqqqq.directscript.lang.data.Literals#EMPTY}, uses the {@link ScriptInstance#getCausedBy()} player's world instead
     *
     * @param name the name
     * @return the world
     */
    public Optional<World> getWorldOrCauserWorld(String name) {
        Literal literal = getLiteral(name);

        if (literal.isEmpty()) {
            Optional<Player> causedBy = this.scriptInstance.getCausedBy();
            if (causedBy.isPresent()) {
                return Optional.of(causedBy.get().getWorld());
            }
            return Optional.absent();
        }
        return DirectScript.instance().getGame().getServer().getWorld(literal.getString());
    }
}
