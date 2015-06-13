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

            if (argument.isMatchName() && !strarg.equals(argument.getName())) {
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

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public Script getScript() {
        return scriptInstance.getScript();
    }

    public Line getLine() {
        return line;
    }

    public Literal[] getLiterals() {
        return literals;
    }

    public Literal getLiteral(int index) {
        return literals[index];
    }

    public Literal getLiteral(int index, Object def) {
        return literals[index].or(def);
    }

    public int getLiteralCount() {
        return literals.length;
    }

    public Result run() {
        return line.getStatement().run(this);
    }

    // Convenience stuff
    public Optional<Player> getPlayerOrCauser(int index) {
        Optional<Player> causedBy = this.scriptInstance.getCausedBy();
        Literal literal = this.literals[index];
        return (literal.isEmpty() ? causedBy : literal.getPlayer());
    }
}
