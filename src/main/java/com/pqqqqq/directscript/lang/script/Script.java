package com.pqqqqq.directscript.lang.script;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.Trigger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 * A piece of code that runs or does something when {@link Trigger}ed
 */
public class Script extends Block {
    private static final Predicate<Line> COMPILE_TIME_PREDICATE = new Predicate<Line>() {

        @Override
        public boolean apply(Line input) {
            Statement.Syntax syntax = input.getStatement().getSyntax();
            return syntax.getExecutionTime() == Statement.ExecutionTime.COMPILE || syntax.getExecutionTime() == Statement.ExecutionTime.ALWAYS;
        }
    };
    private static final Predicate<Line> RUNTIME_PREDICATE = new Predicate<Line>() {

        @Override
        public boolean apply(Line input) {
            Statement.Syntax syntax = input.getStatement().getSyntax();
            return syntax.getExecutionTime() != Statement.ExecutionTime.COMPILE;
        }
    };

    private final ScriptsFile scriptsFile;
    private final String name;
    private final CauseData causeData = new CauseData();

    private Optional<Trigger> trigger;

    /**
     * Creates a new {@link Script} contained in a {@link ScriptsFile} and with a given name
     *
     * @param scriptsFile the file this script comes from
     * @param name        the name of the script
     */
    public Script(ScriptsFile scriptsFile, String name) {
        super(0); // Scripts are always 0 block
        this.scriptsFile = checkNotNull(scriptsFile, "ScriptsFile");
        this.name = checkNotNull(name, "Name");
    }

    /**
     * A {@link Predicate} that applies only to statements with {@link com.pqqqqq.directscript.lang.statement.Statement.Syntax#getExecutionTime()} equal to {@link com.pqqqqq.directscript.lang.statement.Statement.ExecutionTime#COMPILE}
     *
     * @return the predicate
     */
    public static Predicate<Line> compileTimePredicate() {
        return COMPILE_TIME_PREDICATE;
    }

    /**
     * A {@link Predicate} that applies only to statements with {@link com.pqqqqq.directscript.lang.statement.Statement.Syntax#getExecutionTime()} equal to {@link com.pqqqqq.directscript.lang.statement.Statement.ExecutionTime#RUNTIME}
     *
     * @return the predicate
     */
    public static Predicate<Line> runtimePredicate() {
        return RUNTIME_PREDICATE;
    }

    /**
     * Returns the {@link ScriptsFile} this Script comes from
     *
     * @return the scripts file
     */
    public ScriptsFile getScriptsFile() {
        return scriptsFile;
    }

    /**
     * Returns the name of the script
     *
     * @return the name of the script
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link Trigger} that is responsible for activating this Script
     *
     * @return the trigger
     */
    public Optional<Trigger> getTrigger() {
        return trigger;
    }

    /**
     * Sets a new trigger responsible for activating this Script
     *
     * @param trigger the new trigger
     */
    public void setTrigger(Optional<Trigger> trigger) {
        this.trigger = trigger;
    }

    /**
     * Gets this {@link Script}'s {@link CauseData}
     *
     * @return the cause data
     */
    public CauseData getCauseData() {
        return causeData;
    }

    /**
     * Represents this {@link Script}'s cause data
     */
    public class CauseData {
        // Timer cause
        private Long timerDelay = null;
        private Long lastTimerRun = null;

        // Command cause
        private String[] commandAliases = null;

        CauseData() {
        }

        /**
         * Gets the timer delay for {@link com.pqqqqq.directscript.lang.trigger.cause.Cause.TimerCause}
         *
         * @return the timer delay, or null if none
         */
        public Long getTimerDelay() {
            return timerDelay;
        }

        /**
         * Sets the timer delay for {@link com.pqqqqq.directscript.lang.trigger.cause.Cause.TimerCause}
         *
         * @param timerDelay the new timer delay
         */
        public void setTimerDelay(Long timerDelay) {
            this.timerDelay = timerDelay;
        }

        /**
         * Gets the last time this {@link Script} was run as a timer
         *
         * @return the last run as per {@link System#currentTimeMillis()}
         */
        public Long getLastTimerRun() {
            return lastTimerRun;
        }

        /**
         * Sets the last time this {@link Script} was run as a timer to the current time ({@link System#currentTimeMillis()})
         */
        public void setLastTimerRun() {
            this.lastTimerRun = System.currentTimeMillis();
        }

        /**
         * Gets the array of command aliases
         *
         * @return the command aliases, or null if none
         */
        public String[] getCommandAliases() {
            return commandAliases;
        }

        /**
         * Sets the array of command aliases
         *
         * @param commandAliases the new command aliases
         */
        public void setCommandAliases(String[] commandAliases) {
            this.commandAliases = commandAliases;
        }
    }
}
