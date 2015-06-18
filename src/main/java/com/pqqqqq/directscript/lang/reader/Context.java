package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.World;

/**
 * Created by Kevin on 2015-06-12.
 * The context class combines a {@link Line} with its {@link ScriptInstance} to create {@link Statement.Argument} {@link Literal}s
 */
public class Context {
    private final ScriptInstance scriptInstance;
    private final Line line;

    private final String[] stringArguments;
    private final DataContainer[] containers;
    private final Literal[] literals;

    private Statement.Result result = null;

    Context(ScriptInstance scriptInstance, Line line, String[] stringArguments, DataContainer[] containers) {
        this.scriptInstance = scriptInstance;
        this.line = line;

        this.stringArguments = stringArguments;
        this.containers = containers;
        this.literals = new Literal[containers.length];

        for (int i = 0; i < this.literals.length; i++) {
            if (containers[i] != null) {
                this.literals[i] = containers[i].resolve(scriptInstance);
            }
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
     * Gets the {@link DataContainer} argument array
     *
     * @return the data container argument array
     */
    public DataContainer[] getContainers() {
        return containers;
    }

    /**
     * Gets the {@link DataContainer} at the given index
     *
     * @param index the index
     * @return the data argument
     */
    public DataContainer getContainer(int index) {
        return containers[index];
    }

    /**
     * Gets the {@link Literal} argument array
     *
     * @return the argument array
     */
    public Literal[] getLiterals() {
        return literals;
    }

    /**
     * Gets the {@link Literal} at the given index
     *
     * @param index the index
     * @return the literal argument
     */
    public Literal getLiteral(int index) {
        return literals[index];
    }

    /**
     * Gets the {@link Literal} at the given index, or a literal of a default value. This is analogous to: <code>getLiteral(index).or(def)</code>
     *
     * @param index the index of the literal
     * @param def   the default value
     * @return the literal
     */
    public Literal getLiteral(int index, Object def) {
        return literals[index].or(def);
    }

    /**
     * Gets the number of argument {@link Literal}s
     * @return the size of the literal array
     */
    public int getLiteralCount() {
        return literals.length;
    }

    /**
     * Runs this {@link Context}
     * @return the {@link Statement.Result}
     */
    public Statement.Result run() {
        result = line.getStatement().run(this);
        if (scriptInstance.isRuntime() && result != null && !result.isSuccess()) { // We only want this at runtime
            DirectScript.instance().getErrorHandler().log(String.format("Statement in script '%s' -> '%s' at line #%d (script line #%d) failed. Continuing execution.", getScript().getScriptsFile().getStringRepresentation(), getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber()));
            DirectScript.instance().getErrorHandler().flush();
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
     * Gets an {@link Optional} {@link Player} at the index that, if {@link Literal#empty()}, uses the {@link ScriptInstance#getCausedBy()} player instead
     * @param index the index
     * @return the player
     */
    public Optional<Player> getPlayerOrCauser(int index) {
        Optional<Player> causedBy = this.scriptInstance.getCausedBy();
        Literal literal = this.literals[index];
        return (literal.isEmpty() ? causedBy : literal.getPlayer());
    }

    /**
     * Gets an {@link Optional} {@link World} at the index that, if {@link Literal#empty()}, uses the {@link ScriptInstance#getCausedBy()} player's world instead
     *
     * @param index the index
     * @return the world
     */
    public Optional<World> getWorldOrCauserWorld(int index) {
        Literal literal = this.literals[index];

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
