package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import org.spongepowered.api.entity.player.Player;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-12.
 * The context class includes combines a {@link Line} with its {@link ScriptInstance} to create {@link Argument} {@link Literal}s
 */
public class Context {
    private final ScriptInstance scriptInstance;
    private final Line line;
    private Literal[] literals;

    protected Context(ScriptInstance scriptInstance, Line line) {
        this.scriptInstance = scriptInstance;
        this.line = line;

        Argument[] args = line.getStatement().getArguments();
        this.literals = new Literal[args.length];

        int curIndex = 0;
        for (Argument argument : args) {
            String strarg = line.getArg(curIndex);

            if (argument.isModifier() && !strarg.equals(argument.getName())) {
                continue; // Basically skip this argument but keep the string
            }

            if (argument.doParse()) {
                Literal literal = scriptInstance.getSequencer().parse(strarg);
                checkState(argument.isOptional() || !literal.isEmpty(), "Argument " + curIndex + "(" + argument.getName() + ") is not optional.");
                this.literals[curIndex] = literal;
            } else {
                this.literals[curIndex] = Literal.getLiteralBlindly(strarg);
            }
            curIndex++;
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
     * Runs this line. This is analagous to: <code>line.getStatement().run(this)</code>
     * @return the {@link Result}
     */
    public Result run() {
        return line.getStatement().run(this);
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
}
