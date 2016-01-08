package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.exception.MissingInternalBlockException;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-04.
 * A statement that only executes its block code if the condition is true
 */
public class IfStatement extends Statement<Boolean> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("if")
            .suffix("{")
            .arguments(Arguments.of(GenericArguments.withName("Condition")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Block internalBlock = ctx.getLine().getInternalBlock().orElseThrow(() -> new MissingInternalBlockException("If statements must have internal blocks."));
        boolean result = ctx.getLiteral("Condition").getBoolean();

        if (result) {
            internalBlock.toRunnable(ctx.getScriptInstance()).execute();
        }

        return Result.<Boolean>builder().success().result(result).build();
    }
}
