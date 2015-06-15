package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.player.Player;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-12.
 * The context class combines a {@link Line} with its {@link ScriptInstance} to create {@link Statement.Argument} {@link Literal}s
 */
public class Context {
    private final ScriptInstance scriptInstance;
    private final Line line;
    private Literal[] literals;

    protected Context(ScriptInstance scriptInstance, Line line) {
        this.scriptInstance = scriptInstance;
        this.line = line;

        Statement.Argument[] args = line.getStatement().getArguments();
        this.literals = new Literal[args.length];

        int curIndex = 0;
        for (Statement.Argument argument : args) {
            if (line.getArgCount() <= curIndex) { // If it goes over, just put empty literals
                this.literals[curIndex++] = Literal.empty();
                continue;
            }

            String strarg = argument.isRest() ? StringUtils.join(line.getArguments(), line.getStatement().getSplitString(), curIndex, line.getArguments().length) : line.getArg(curIndex);
            Literal litarg = argument.doParse() ? scriptInstance.getSequencer().parse(strarg) : Literal.getLiteralBlindly(strarg); // Use doParse boolean
            checkState(argument.isOptional() || !litarg.isEmpty(), "Argument " + curIndex + "(" + argument.getName() + ") is not optional."); // Use isOptional boolean

            if (argument.isModifier() && !litarg.getString().equals(argument.getName())) { // Use isModifier boolean
                continue; // Basically skip this argument but keep the string
            }

            this.literals[curIndex] = litarg;
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
     * @return the {@link Statement.Result}
     */
    public Statement.Result run() {
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
