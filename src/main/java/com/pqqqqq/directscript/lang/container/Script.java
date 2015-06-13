package com.pqqqqq.directscript.lang.container;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 * A piece of code that runs or does something when {@link Trigger}ed
 */
public class Script {
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

    private final List<Line> lines = new ArrayList<Line>();
    private final BiMap<Line, Line> linkedLines = HashBiMap.create();

    private Optional<Trigger> trigger;

    /**
     * Creates a new {@link Script} contained in a {@link ScriptsFile} and with a given name
     *
     * @param scriptsFile the file this script comes from
     * @param name        the name of the script
     */
    public Script(ScriptsFile scriptsFile, String name) {
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
     * Returns a {@link List} of {@link Line}s that are a part of this Script
     *
     * @return a list of lines
     */
    public List<Line> getLines() {
        return lines;
    }

    /**
     * Returns a {@link BiMap}, a bidirectional map that links the starting and ending {@link Line} braces
     * @return the map
     */
    public BiMap<Line, Line> getLinkedLines() {
        return linkedLines;
    }

    // Convenience methods so I don't get confused

    /**
     * Returns the closing brace {@link Line} for the given line
     * @param starting the opening brace line
     * @return the closing brace line
     */
    public Line lookupEndingLine(Line starting) {
        return linkedLines.get(starting);
    }

    /**
     * Returns the starting brace {@link Line} for the given line
     * @param ending the closing brace line
     * @return the opening brace line
     */
    public Line lookupStartingLine(Line ending) {
        return linkedLines.inverse().get(ending);
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
