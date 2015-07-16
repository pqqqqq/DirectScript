package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-04.
 * A statement that only executes its block code if the condition is true
 */
public class IfStatement extends Statement<Boolean> {

    public IfStatement() {
        super(Syntax.builder()
                .identifiers("if")
                .suffix("{")
                .arguments(Arguments.of(Argument.from("Condition")))
                .build());
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Block internalBlock = checkNotNull(ctx.getLine().getInternalBlock(), "This line has no internal block");
        boolean result = ctx.getLiteral("Condition").getBoolean();

        if (result) {
            internalBlock.toRunnable(ctx.getScriptInstance()).execute();
        }

        return Result.<Boolean>builder().success().result(result).build();
    }
}
