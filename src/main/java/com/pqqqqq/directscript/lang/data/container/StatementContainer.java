package com.pqqqqq.directscript.lang.data.container;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link Line} {@link Statement} {@link DataContainer} which resolves a statement's {@link Literal} value at runtime
 */
public class StatementContainer implements DataContainer {
    private final Line statement;

    /**
     * Creates a new {@link StatementContainer} instance with the given statement in string form
     *
     * @param statement the statement
     */
    public StatementContainer(Line statement) {
        this.statement = statement;
    }

    /**
     * Gets the {@link Line} statement string associated with this {@link StatementContainer}
     *
     * @return the statement
     */
    public Line getStatement() {
        return statement;
    }

    @Override
    public Literal resolve(ScriptInstance scriptInstance) {
        Statement.Result result = getStatement().toContext(scriptInstance).run();

        Optional<Literal> literalOptional = result.getLiteralResult();
        checkState(literalOptional.isPresent(), String.format("%s is a void statement, and did not return a type.", getStatement().getStatement().getClass().getName()));

        return literalOptional.get();
    }
}
