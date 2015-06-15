package com.pqqqqq.directscript.lang.script;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.Trigger;

/**
 * Created by Kevin on 2015-06-02.
 * A piece of code that runs or does something when {@link Trigger}ed
 */
public class Script extends Block {
    private static final Predicate<Line> COMPILETIME_PREDICATE = new Predicate<Line>() {

        public boolean apply(Line input) {
            Statement statement = input.getStatement();
            return statement.getExecutionTime() == Statement.ExecutionTime.COMPILE || statement.getExecutionTime() == Statement.ExecutionTime.ALWAYS;
        }
    };
    private static final Predicate<Line> RUNTIME_PREDICATE = new Predicate<Line>() {

        public boolean apply(Line input) {
            Statement statement = input.getStatement();
            return statement.getExecutionTime() != Statement.ExecutionTime.COMPILE;
        }
    };

    private final ScriptsFile scriptsFile;
    private final String name;

    private Optional<Trigger> trigger;

    /**
     * Creates a new {@link Script} contained in a {@link ScriptsFile} and with a given name
     *
     * @param scriptsFile the file this script comes from
     * @param name        the name of the script
     */
    public Script(ScriptsFile scriptsFile, String name) {
        super(); // Constructs an empty block with no parent
        this.scriptsFile = scriptsFile;
        this.name = name;
    }

    /**
     * A {@link Predicate} that applies only to statements with {@link Statement#getExecutionTime()} equal to {@link com.pqqqqq.directscript.lang.statement.Statement.ExecutionTime#COMPILE}
     *
     * @return the predicate
     */
    public static Predicate<Line> compileTimePredicate() {
        return COMPILETIME_PREDICATE;
    }

    /**
     * A {@link Predicate} that applies only to statements with {@link Statement#getExecutionTime()} equal to {@link com.pqqqqq.directscript.lang.statement.Statement.ExecutionTime#RUNTIME}
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
     * @return the trigger
     */
    public Optional<Trigger> getTrigger() {
        return trigger;
    }

    /**
     * Sets a new trigger responsible for activating this Script
     * @param trigger the new trigger
     */
    public void setTrigger(Optional<Trigger> trigger) {
        this.trigger = trigger;
    }
}
