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

    public Script(ScriptsFile scriptsFile, String name) {
        this.scriptsFile = scriptsFile;
        this.name = name;
    }

    public static Predicate<Line> compileTimePredicate() {
        return COMPILETIME_PREDICATE;
    }

    public static Predicate<Line> runtimePredicate() {
        return RUNTIME_PREDICATE;
    }

    public ScriptsFile getScriptsFile() {
        return scriptsFile;
    }

    public String getName() {
        return name;
    }

    public List<Line> getLines() {
        return lines;
    }

    public BiMap<Line, Line> getLinkedLines() {
        return linkedLines;
    }

    // Convenience methods so I don't get confused
    public Line lookupEndingLine(Line starting) {
        return linkedLines.get(starting);
    }

    public Line lookupStartingLine(Line ending) {
        return linkedLines.inverse().get(ending);
    }

    public Optional<Trigger> getTrigger() {
        return trigger;
    }

    public void setTrigger(Optional<Trigger> trigger) {
        this.trigger = trigger;
    }
}
