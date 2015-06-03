package com.pqqqqq.directscript.lang.container;

import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Sequencer;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.env.Environment;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
public class ScriptInstance implements Environment {
    private static final ScriptInstance COMPILE = builder().script(null).cause(Causes.COMPILE).predicate(Script.compileTimePredicate()).build();

    @Nonnull private final Script script;
    @Nonnull private final Cause cause;
    @Nonnull private final Predicate<Line> linePredicate;
    @Nonnull private final Sequencer sequencer;
    @Nonnull private final Map<String, Variable> variableMap;

    ScriptInstance(Script script, Cause cause, Predicate<Line> linePredicate, Map<String, Variable> variableMap) {
        this.script = script;
        this.cause = cause;
        this.linePredicate = linePredicate;
        this.sequencer = Sequencer.instance(this);
        this.variableMap = variableMap;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ScriptInstance compile() {
        return COMPILE;
    }

    public Map<String, Variable> getVariables() {
        return variableMap;
    }

    public Cause getCause() {
        return null;
    }

    public Script getScript() {
        return script;
    }

    public Predicate<Line> getLinePredicate() {
        return linePredicate;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public static class Builder {
        private Script script = null;
        private Cause cause = null;
        private Predicate<Line> linePredicate = Script.runtimePredicate();
        private Map<String, Variable> variableMap = new HashMap<String, Variable>();

        Builder() { // Default view
            variables();
        }

        public Builder script(Script script) {
            this.script = script;
            return this;
        }

        public Builder cause(Cause cause) {
            this.cause = cause;
            return this;
        }

        public Builder predicate(Predicate<Line> linePredicate) {
            this.linePredicate = linePredicate;
            return this;
        }

        public Builder variables(Map<String, Variable> variableMap) {
            this.variableMap.putAll(variableMap);
            return this;
        }

        public Builder variables(Variable... variables) {
            Map<String, Variable> variableMap = new HashMap<String, Variable>();
            for (Variable variable : variables) {
                variableMap.put(variable.getName(), variable);
            }

            return variables(variableMap);
        }

        private Builder variables() { // Adds generic variables for script (run on build)
            return variables(new Variable("GENERIC:millis", Literal.getLiteralBlindly(System.currentTimeMillis())));
        }

        public Builder variables(Player player) { // Adds sponge variables for a player
            return variables(new Variable("SPONGE:playername", Literal.getLiteralBlindly(player.getName())), new Variable("SPONGE:playeruuid", Literal.getLiteralBlindly(player.getIdentifier())));
        }

        public Builder copy() {
            return new Builder().script(script).cause(cause).predicate(linePredicate).variables(variableMap);
        }

        public ScriptInstance build() {
            checkNotNull(cause, "Cause cannot be null");
            checkState(script != null || cause.equals(Causes.COMPILE), "Script cannot be null");

            return new ScriptInstance(script, cause, linePredicate, variableMap);
        }
    }
}
