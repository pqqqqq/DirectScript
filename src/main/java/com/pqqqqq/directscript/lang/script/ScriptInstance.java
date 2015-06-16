package com.pqqqqq.directscript.lang.script;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Sequencer;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.generic.setters.BreakStatement;
import com.pqqqqq.directscript.lang.statement.generic.setters.ContinueStatement;
import com.pqqqqq.directscript.lang.statement.internal.setters.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import com.pqqqqq.directscript.lang.util.ICopyable;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.util.command.CommandSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a running instance of a {@link Script} that can be executed
 */
public class ScriptInstance implements Runnable {
    private static final Builder COMPILE = builder().cause(Causes.COMPILE).predicate(Script.compileTimePredicate());

    private final Script script;
    private final Cause cause;
    private final Predicate<Line> linePredicate;
    private final Sequencer sequencer;
    private final Optional<Event> event;
    private final Optional<Player> causedBy;

    private final Set<Context> contextSet = new HashSet<Context>();
    private final Environment environment = new Environment(this);

    private Optional<Line> currentLine = Optional.absent();
    private Optional<Literal> returnValue = Optional.absent();

    private boolean skipLines = false;
    private Line skipToLine = null;

    ScriptInstance(Script script, Cause cause, Predicate<Line> linePredicate, Map<String, Variable> variableMap, Event event, Player causedBy) {
        this.script = script;
        this.cause = cause;
        this.linePredicate = linePredicate;
        this.sequencer = Sequencer.instance(this);
        this.event = Optional.fromNullable(event);
        this.causedBy = Optional.fromNullable(causedBy);
        getEnvironment().getVariables().putAll(variableMap);
    }

    /**
     * Retrieves an instance of a {@link ScriptInstance} {@link com.pqqqqq.directscript.lang.script.ScriptInstance.Builder}
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Retrieves the {@link ScriptInstance} for a {@link Script} with the compile time predicate
     *
     * @param script the script
     * @return the new script instance
     * @see Script#compileTimePredicate()
     */
    public static ScriptInstance compile(Script script) {
        return COMPILE.copy().script(script).build();
    }

    /**
     * Gets the {@link Cause} that triggered this {@link ScriptInstance}
     *
     * @return the cause
     */
    public Cause getCause() {
        return null;
    }

    /**
     * Gets the {@link Script} this {@link ScriptInstance} is attached to
     *
     * @return the script
     */
    public Script getScript() {
        return script;
    }

    /**
     * Gets the line {@link Predicate} that is applied to {@link Line}s before their execution
     *
     * @return the predicate
     */
    public Predicate<Line> getLinePredicate() {
        return linePredicate;
    }

    /**
     * Gets the specific {@link Sequencer} for this {@link ScriptInstance}
     *
     * @return the sequencer
     */
    public Sequencer getSequencer() {
        return sequencer;
    }

    /**
     * Gets the {@link Optional} {@link Event} that triggered this {@link ScriptInstance}
     * @return the event
     */
    public Optional<Event> getEvent() {
        return event;
    }

    /**
     * Gets the {@link Optional} {@link Player} who caused the triggering of this {@link ScriptInstance}
     * @return the player
     */
    public Optional<Player> getCausedBy() {
        return causedBy;
    }

    /**
     * Gets the {@link Optional} current {@link Line} in execution
     * @return the current line
     */
    public Optional<Line> getCurrentLine() {
        return currentLine;
    }

    /**
     * Gets whether this {@link ScriptInstance} is current skipping line execution
     * @return true if skipping execution
     */
    public boolean doSkipLines() {
        return skipLines;
    }

    /**
     * Sets whether this {@link ScriptInstance} shoudl skip line execution
     * @param skipLines the new skip value boolean
     */
    public void setSkipLines(boolean skipLines) {
        this.skipLines = skipLines;
    }

    /**
     * Gets the {@link Line} that needs to be reached before skipping lines is toggled off
     *
     * @return the skip to line
     */
    public Line getSkipToLine() {
        return skipToLine;
    }

    /**
     * Sets the {@link Line} that needs to be reached before skipping lines is toggled off
     *
     * @param skipToLine the new skip to line
     */
    public void setSkipToLine(Line skipToLine) {
        this.skipToLine = skipToLine;
        setSkipLines(skipToLine != null);
    }

    /**
     * Gets the {@link Optional} {@link Literal} for the return value of this {@link ScriptInstance}
     * @return the literal return value
     */
    public Optional<Literal> getReturnValue() {
        return returnValue;
    }

    /**
     * Sets the {@link Literal} return value for this {@link ScriptInstance}
     * @param returnValue the new return value
     */
    public void setReturnValue(Optional<Literal> returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * Gets a {@link Set} of {@link Context}s for lines that have already been executed in this {@link ScriptInstance}
     * @return the set
     */
    public Set<Context> getContextSet() {
        return contextSet;
    }

    /**
     * Gets the {@link Statement.Result} of a specific {@link Line}.
     * @param line the line to check
     * @return the result, or null if not run yet
     */
    public Statement.Result getResultOf(Line line) {
        for (Context context : getContextSet()) {
            if (context.getLine().equals(line)) {
                return context.getResult();
            }
        }
        return null;
    }

    /**
     * Gets this {@link ScriptInstance}'s {@link Environment}
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Executes a {@link Block} with the {@link ScriptInstance}
     * @param block the block
     */
    public Result execute(Block block) {
        checkNotNull(block, "Block cannot be null");
        for (Line line : block) {
            try {
                if (getReturnValue().isPresent()) {
                    return Result.SUCCESS; // Return if execution is halted
                }

                if (getSkipToLine() != null) {
                    if (!getSkipToLine().equals(line)) {
                        continue;
                    }
                    setSkipToLine(null);
                }

                if (getLinePredicate().apply(line)) {
                    this.currentLine = Optional.of(line); // Set current line

                    Statement statement = line.getStatement();
                    if (!doSkipLines() || statement instanceof Termination) {
                        // Break and continue get special treatment
                        if (statement instanceof BreakStatement) {
                            return Result.FAILURE_BREAK;
                        }

                        if (statement instanceof ContinueStatement) {
                            return Result.FAILURE_CONTINUE;
                        }

                        Context ctx = line.toContex(this);
                        ctx.run();
                        getContextSet().add(ctx); // Add to context set
                    }
                }
            } catch (Throwable e) {
                DirectScript.instance().getErrorHandler().log(String.format("Error in script '%s' -> '%s' at line #%d (script line #%d): ", getScript().getScriptsFile().getStringRepresentation(), getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber()));
                DirectScript.instance().getErrorHandler().log(e);
                DirectScript.instance().getErrorHandler().flush();
                return Result.FAILURE_ERROR; // Stop running of script
            }
        }
        return Result.SUCCESS;
    }

    /**
     * Executes the {@link Script}'s {@link Block}
     */
    public Result execute() {
        return execute(getScript()); // Runs the script's block
    }

    public void run() {
        execute(); // Override for Runnable, just perform the execute() method
    }

    /**
     * An enumeration of the result of a {@link Block} run
     */
    public enum Result {
        /**
         * Represents a successful run
         */
        SUCCESS,

        /**
         * Represents a run that was failed due to an error
         */
        FAILURE_ERROR,

        /**
         * Represents a run that was failed due to being broken
         */
        FAILURE_BREAK,

        /**
         * Represents a run that was failed due to being continued
         */
        FAILURE_CONTINUE
    }

    /**
     * The builder class for {@link ScriptInstance}
     */
    public static class Builder implements ICopyable<Builder> {
        private Script script = null;
        private Cause cause = null;
        private Predicate<Line> linePredicate = Script.runtimePredicate();
        private Map<String, Variable> variableMap = new HashMap<String, Variable>();
        private Event event = null;
        private Player causedBy = null;

        Builder() { // Default view
        }

        /**
         * Sets the {@link Script} for this {@link ScriptInstance} builder
         * @param script the script
         * @return this builder, for fluency
         * @see ScriptInstance#getScript()
         */
        public Builder script(Script script) {
            this.script = script;
            return this;
        }

        /**
         * Sets the {@link Cause} for this {@link ScriptInstance} builder
         * @param cause the cause
         * @return this builder, for fluency
         * @see ScriptInstance#getCause()
         */
        public Builder cause(Cause cause) {
            this.cause = cause;
            return this;
        }

        /**
         * Sets the {@link Line} {@link Predicate} for this {@link ScriptInstance} builder
         * @param linePredicate the line predicate
         * @return this builder, for fluency
         * @see ScriptInstance#getLinePredicate()
         */
        public Builder predicate(Predicate<Line> linePredicate) {
            this.linePredicate = linePredicate;
            return this;
        }

        /**
         * Sets the {@link Variable} {@link Map} ({@link String} vs Variable) for this {@link ScriptInstance} builder
         * @param variableMap the variable map
         * @return this builder, for fluency
         */
        public Builder variables(Map<String, Variable> variableMap) {
            this.variableMap.putAll(variableMap);
            return this;
        }

        /**
         * Sets the {@link Variable} {@link Map} ({@link String} vs Variable) for this {@link ScriptInstance} builder
         * @param variables the variable array
         * @return this builder, for fluency
         */
        public Builder variables(Variable... variables) {
            Map<String, Variable> variableMap = new HashMap<String, Variable>();
            for (Variable variable : variables) {
                variableMap.put(variable.getName(), variable);
            }

            return variables(variableMap);
        }

        /**
         * Applies {@link Variable}s associated with the given {@link CommandSource}
         * @param source the command source
         * @return this builder, for fluency
         */
        public Builder variables(CommandSource source) { // Adds sponge variables for a command source
            return variables(new Variable("sponge.sourcename", Literal.getLiteralBlindly(source.getName()), true));
        }

        /**
         * Sets the {@link Event} for this {@link ScriptInstance} builder
         *
         * @param event the event
         * @return this builder, for fluency
         * @see ScriptInstance#getEvent()
         */
        public Builder event(Event event) {
            this.event = event;
            return this;
        }

        /**
         * Sets the caused by {@link Player} this {@link ScriptInstance} builder
         *
         * @param causedBy the player
         * @return this builder, for fluency
         * @see ScriptInstance#getCausedBy()
         */
        public Builder causedBy(Player causedBy) {
            this.causedBy = causedBy;
            return this;
        }

        /**
         * Copies the builder in its current state
         * @return the copied builder
         */
        public Builder copy() {
            return new Builder().script(script).cause(cause).predicate(linePredicate).variables(variableMap).event(event).causedBy(causedBy);
        }

        /**
         * Builds the {@link ScriptInstance}
         * @return the new script instance
         */
        public ScriptInstance build() {
            checkNotNull(cause, "Cause cannot be null");
            checkNotNull(linePredicate, "Predicate cannot be null");
            checkState(script != null || cause.equals(Causes.COMPILE), "Script cannot be null");

            variables(); // Generic variables
            return new ScriptInstance(script, cause, linePredicate, variableMap, event, causedBy);
        }

        private Builder variables() { // Adds generic variables for script (run on build)
            return variables(new Variable("generic.cause", Literal.getLiteralBlindly(cause.getName()), true), new Variable("generic.millis", Literal.getLiteralBlindly(System.currentTimeMillis()), true));
        }
    }
}
