package com.pqqqqq.directscript.lang.script;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.generic.setters.BreakStatement;
import com.pqqqqq.directscript.lang.statement.generic.setters.ContinueStatement;
import com.pqqqqq.directscript.lang.statement.generic.setters.ElseStatement;
import com.pqqqqq.directscript.lang.statement.internal.setters.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import com.pqqqqq.directscript.lang.util.ICopyable;
import org.spongepowered.api.event.Event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a running instance of a {@link Environment} {@link Script} that can be executed
 */
public class ScriptInstance extends Environment implements Runnable {
    private static final Builder COMPILE = builder().cause(Causes.COMPILE).predicate(Script.compileTimePredicate());

    private final Script script;
    private final Cause cause;
    private final Predicate<Line> linePredicate;
    private final Optional<Event> event;
    private final Map<String, Object> eventVars;

    private final Set<Context> contextSet = new HashSet<Context>();

    private Optional<Line> currentLine = Optional.absent();
    private Optional<Literal> returnValue = Optional.absent();

    private boolean skipLines = false;
    private Line skipToLine = null;

    ScriptInstance(Script script, Cause cause, Predicate<Line> linePredicate, Event event, Map<String, Object> eventVars) {
        super((script == null ? null : script.getScriptsFile())); // The parent is the file
        this.script = script;
        this.cause = cause;
        this.linePredicate = linePredicate;

        this.event = Optional.fromNullable(event);
        this.eventVars = eventVars;
    }

    /**
     * Retrieves an instance of a {@link ScriptInstance} {@link com.pqqqqq.directscript.lang.script.ScriptInstance.Builder}
     *
     * @return a new builder
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
        return cause;
    }

    /**
     * Gets if this {@link ScriptInstance} is at runtime execution
     *
     * @return true if at runtime
     */
    public boolean isRuntime() {
        return cause != Causes.COMPILE;
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
     * Gets the {@link Optional} {@link Event} that triggered this {@link ScriptInstance}
     *
     * @return the event
     */
    public Optional<Event> getEvent() {
        return event;
    }

    /**
     * Gets the String-Object event var {@link Map} for this {@link ScriptInstance} as per its {@link com.pqqqqq.directscript.lang.trigger.Trigger Trigger}
     * @return the object map
     */
    public Map<String, Object> getEventVars() {
        return eventVars;
    }

    /**
     * Gets the event var that is closest in subclasses to the given type
     * @param type the type
     * @param <T> the generic type parameter
     * @return the closest event var, or null if none
     */
    public <T> T getEventVarWithType(Class<T> type) {
        T current = null;
        for (Object value : getEventVars().values()) {
            if (type.isAssignableFrom(value.getClass())) {
                if (current == null || !current.getClass().equals(value.getClass()) && !current.getClass().isAssignableFrom(value.getClass())) {
                    current = (T) value;
                }
            }
        }

        return current;
    }

    /**
     * Gets the {@link Optional} current {@link Line} in execution
     *
     * @return the current line
     */
    public Optional<Line> getCurrentLine() {
        return currentLine;
    }

    /**
     * Gets whether this {@link ScriptInstance} is current skipping line execution
     *
     * @return true if skipping execution
     */
    public boolean doSkipLines() {
        return skipLines;
    }

    /**
     * Sets whether this {@link ScriptInstance} should skip line execution
     *
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
     *
     * @return the literal return value
     */
    public Optional<Literal> getReturnValue() {
        return returnValue;
    }

    /**
     * Sets the {@link Literal} return value for this {@link ScriptInstance}
     *
     * @param returnValue the new return value
     */
    public void setReturnValue(Optional<Literal> returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * Gets a {@link Set} of {@link Context}s for lines that have already been executed in this {@link ScriptInstance}
     *
     * @return the set
     */
    public Set<Context> getContextSet() {
        return contextSet;
    }

    /**
     * Gets the {@link Statement.Result} of a specific {@link Line}.
     *
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
     * Executes a {@link Block} with the {@link ScriptInstance}
     *
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
                    if (!doSkipLines() || statement instanceof Termination || statement instanceof ElseStatement) {
                        // Break and continue get special treatment
                        if (statement instanceof BreakStatement) {
                            return Result.FAILURE_BREAK;
                        }

                        if (statement instanceof ContinueStatement) {
                            return Result.FAILURE_CONTINUE;
                        }

                        Context ctx = line.toContext(this);
                        ctx.run();
                        getContextSet().add(ctx); // Add to context set
                    }
                }
            } catch (Throwable e) {
                Lang.instance().errorHandler().log(String.format("Error in script '%s' -> '%s' at line #%d (script line #%d): ", getScript().getScriptsFile().getStringRepresentation(), getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber()));
                Lang.instance().errorHandler().log(e);
                Lang.instance().errorHandler().flush();
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

    @Override
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
        private Event event = null;
        private Map<String, Object> eventVars = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER); // Ignore cases with #get

        Builder() { // Default view
        }

        /**
         * Sets the {@link Script} for this {@link ScriptInstance} builder
         *
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
         *
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
         *
         * @param linePredicate the line predicate
         * @return this builder, for fluency
         * @see ScriptInstance#getLinePredicate()
         */
        public Builder predicate(Predicate<Line> linePredicate) {
            this.linePredicate = linePredicate;
            return this;
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
         * Puts all the object entries to this Builder
         * @param eventVars the event var map to put
         * @return this builder, fo fluency
         * @see ScriptInstance#getEventVars()
         */
        public Builder eventVar(Map<String, Object> eventVars) {
            this.eventVars.putAll(eventVars);
            return this;
        }

        /**
         * Puts a single entry to the object map of this Builder
         * @param key the key
         * @param value the value
         * @return this builder, for fluency
         */
        public Builder eventVar(String key, Object value) {
            if (key != null && value != null) {
                this.eventVars.put(key, value);
            }
            return this;
        }

        /**
         * Copies the builder in its current state
         *
         * @return the copied builder
         */
        @Override
        public Builder copy() {
            return new Builder().script(script).cause(cause).predicate(linePredicate).event(event).eventVar(eventVars);
        }

        /**
         * Builds the {@link ScriptInstance}
         *
         * @return the new script instance
         */
        public ScriptInstance build() {
            checkNotNull(cause, "Cause cannot be null");
            checkNotNull(linePredicate, "Predicate cannot be null");
            checkState(script != null || cause.equals(Causes.COMPILE), "Script cannot be null");

            this.eventVars.put("Cause", cause.getNames()[0]); // Add cause to eventvars
            return new ScriptInstance(script, cause, linePredicate, event, eventVars);
        }
    }
}
