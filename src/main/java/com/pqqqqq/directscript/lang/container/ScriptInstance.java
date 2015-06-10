package com.pqqqqq.directscript.lang.container;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Sequencer;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.env.Environment;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.setters.internal.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.util.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
public class ScriptInstance extends Environment implements Runnable {
    private static final Builder COMPILE = builder().cause(Causes.COMPILE).predicate(Script.compileTimePredicate());

    @Nonnull private final Script script;
    @Nonnull private final Cause cause;
    @Nonnull private final Predicate<Line> linePredicate;
    @Nonnull private final Sequencer sequencer;
    @Nonnull
    private final Optional<Event> event;
    @Nonnull
    private final Optional<Player> causedBy;

    @Nonnull
    private final Map<Line, StatementResult> resultMap = Maps.newHashMap();
    @Nonnull
    private Optional<Line> currentLine = Optional.absent();
    private boolean skipLines = false;
    private boolean haltExecution = false;

    ScriptInstance(Script script, Cause cause, Predicate<Line> linePredicate, Map<String, Variable> variableMap, Event event, Player causedBy) {
        super(variableMap);
        this.script = script;
        this.cause = cause;
        this.linePredicate = linePredicate;
        this.sequencer = Sequencer.instance(this);
        this.event = Optional.fromNullable(event);
        this.causedBy = Optional.fromNullable(causedBy);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ScriptInstance compile(Script script) {
        return COMPILE.copy().script(script).build();
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

    public Optional<Event> getEvent() {
        return event;
    }

    public Optional<Player> getCausedBy() {
        return causedBy;
    }

    public Optional<Line> getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(Optional<Line> currentLine) {
        this.currentLine = currentLine;
    }

    public boolean doSkipLines() {
        return skipLines;
    }

    public void setSkipLines(boolean skipLines) {
        this.skipLines = skipLines;
    }

    public boolean doHaltExecution() {
        return haltExecution;
    }

    public void setHaltExecution(boolean haltExecution) {
        this.haltExecution = haltExecution;
    }

    public Map<Line, StatementResult> getResultMap() {
        return resultMap;
    }

    public StatementResult getResultOf(Line line) {
        return resultMap.get(line);
    }

    // Run the container
    public void run() {
        checkNotNull(getScript(), "Script cannot be null");
        for (Line line : getScript().getLines()) {
            try {
                if (doHaltExecution()) {
                    return; // Return if execution is halted
                }

                if (getLinePredicate().apply(line)) {
                    setCurrentLine(Optional.of(line)); // Set current line

                    IStatement statement = line.getIStatement();
                    if (!doSkipLines() || statement instanceof Termination) {
                        getResultMap().put(line, line.toContainer(this).run()); // Add to result map
                    }
                }
            } catch (Throwable e) {
                DirectScript.instance().getErrorHandler().log(String.format("Error in script '%s' -> '%s' at line #%d (script line #%d): ", getScript().getScriptsFile().getStringRepresentation(), getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber()));
                DirectScript.instance().getErrorHandler().log(e);
                DirectScript.instance().getErrorHandler().flush();
            }
        }
    }

    public static class Builder {
        private Script script = null;
        private Cause cause = null;
        private Predicate<Line> linePredicate = Script.runtimePredicate();
        private Map<String, Variable> variableMap = new HashMap<String, Variable>();
        private Event event = null;
        private Player causedBy = null;

        Builder() { // Default view
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

        public Builder event(Event event) {
            this.event = event;
            return this;
        }

        public Builder causedBy(Player causedBy) {
            this.causedBy = causedBy;
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
            return variables(new Variable("GENERIC.cause", Literal.getLiteralBlindly(cause.getCause()), true), new Variable("GENERIC.millis", Literal.getLiteralBlindly(System.currentTimeMillis()), true));
        }

        public Builder variables(Player player) { // Adds sponge variables for a player
            return variables(new Variable("SPONGE.playername", Literal.getLiteralBlindly(player.getName()), true), new Variable("SPONGE.playeruuid", Literal.getLiteralBlindly(player.getIdentifier()), true));
        }

        public Builder variables(CommandSource source) { // Adds sponge variables for a command source
            if (source instanceof Player) {
                variables((Player) source);
            }

            return variables(new Variable("SPONGE.sourcename", Literal.getLiteralBlindly(source.getName()), true));
        }

        public Builder copy() {
            return new Builder().script(script).cause(cause).predicate(linePredicate).variables(variableMap).event(event).causedBy(causedBy);
        }

        public ScriptInstance build() {
            checkNotNull(cause, "Cause cannot be null");
            checkNotNull(linePredicate, "Predicate cannot be null");
            checkState(script != null || cause.equals(Causes.COMPILE), "Script cannot be null");

            variables(); // Generic variables
            return new ScriptInstance(script, cause, linePredicate, variableMap, event, causedBy);
        }
    }
}
