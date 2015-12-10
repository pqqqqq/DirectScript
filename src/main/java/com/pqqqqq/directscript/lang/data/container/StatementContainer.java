package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link DataContainer} which resolves a statement's {@link Literal} value at runtime
 */
public class StatementContainer implements DataContainer {
    private final DataContainer statement;

    /**
     * Creates a new {@link StatementContainer} instance with the given statement in string form
     *
     * @param statement the statement
     */
    public StatementContainer(DataContainer statement) {
        this.statement = statement;
    }

    /**
     * Gets the {@link DataContainer} statement string associated with this {@link StatementContainer}
     *
     * @return the statement
     */
    public DataContainer getStatement() {
        return statement;
    }

    @Override
    public Literal resolve(Context ctx) {
        Optional<Block.BlockRunnable> blockRunnable = ctx.getScriptInstance().getCurrentRunnable();
        Line currentLine = (blockRunnable.isPresent() ? blockRunnable.get().getCurrentLine() : ctx.getLine());

        Line line = new Line(currentLine.getAbsoluteNumber(), currentLine.getScriptNumber(), getStatement().resolve(ctx).get().getString());
        line.setDepth(currentLine.getDepth());
        line.setInternalBlock(currentLine.getInternalBlock());

        Statement.Result result = line.toContext(ctx.getScriptInstance()).run();
        Optional<Literal> literalOptional = result.getLiteralResult();
        return literalOptional.orElse(Literal.Literals.EMPTY);
    }
}
