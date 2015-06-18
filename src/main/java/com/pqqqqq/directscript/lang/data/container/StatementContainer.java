package com.pqqqqq.directscript.lang.data.container;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link Statement} {@link DataContainer} which resolves the line's {@link Literal} value at runtime
 */
public class StatementContainer implements DataContainer {
    private final String statement;

    /**
     * Creates a new {@link StatementContainer} instance with the given statement in string form
     *
     * @param statement the statement
     */
    public StatementContainer(String statement) {
        this.statement = statement;
    }

    /**
     * Gets the statement string associated with this {@link StatementContainer}
     *
     * @return the statement
     */
    public String getStatement() {
        return statement;
    }

    public Literal resolve(ScriptInstance scriptInstance) {
        Optional<Line> currentLine = scriptInstance.getCurrentLine();
        checkState(currentLine.isPresent(), "No current line could be found");

        Line line = new Line(currentLine.get().getAbsoluteNumber(), currentLine.get().getScriptNumber(), getStatement());
        Statement.Result result = line.toContex(scriptInstance).run();

        Optional<Literal> literalOptional = result.getLiteralResult();
        checkState(literalOptional.isPresent(), String.format("%s is a void statement, and did not return a type.", line.getStatement().getClass().getName()));

        return literalOptional.get();
    }
}
