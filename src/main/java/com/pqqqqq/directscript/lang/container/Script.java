package com.pqqqqq.directscript.lang.container;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.error.ErrorHandler;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 * A piece of code that runs or does something when {@link Trigger}ed
 */
public class Script {
    private static final Predicate<Line> COMPILETIME_PREDICATE = new Predicate<Line>() {

        public boolean apply(Line input) {
            Optional<Statement> statement = Statements.getStatementFromLine(input);
            return statement.isPresent() && statement.get().compileTime();
        }
    };
    private static final Predicate<Line> RUNTIME_PREDICATE = new Predicate<Line>() {

        public boolean apply(Line input) {
            Optional<Statement> statement = Statements.getStatementFromLine(input);
            return !statement.isPresent() || !statement.get().compileTime();
        }
    };
    private final ScriptsFile scriptsFile;
    private final String name;
    private final List<Line> lines = new ArrayList<Line>();

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

    // Tentative method, run the container
    public void run(Cause fromCause) {
        run(fromCause, runtimePredicate());
    }

    public void run(Cause fromCause, Predicate<Line> filter) {
        for (Line line : lines) {
            try {
                if (filter == null || filter.apply(line)) {
                    Optional<IStatement> statement = Statements.getIStatementFromLine(line);
                    if (!statement.isPresent()) {
                        throw new IllegalStateException("Unknown statement");
                    }

                    statement.get().run(this, line);
                }
            } catch (Throwable e) {
                ErrorHandler.instance().log(String.format("Error in script '%s' -> '%s' at line #%d (script line #%d): ", scriptsFile.getStringRepresentation(), name, line.getAbsoluteNumber(), line.getScriptNumber()));
                ErrorHandler.instance().log(e);
                ErrorHandler.instance().flush();
            }
        }
    }
}
