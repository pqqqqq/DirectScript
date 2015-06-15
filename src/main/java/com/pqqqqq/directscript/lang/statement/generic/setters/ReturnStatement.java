package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that returns, and ceases execution, of the script
 */
public class ReturnStatement extends Statement {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"return", "exit"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("ReturnValue").optional().build()
        };
    }

    @Override
    public Result run(Context ctx) {
        Literal returnValue = ctx.getLiteral(0);
        ctx.getScriptInstance().setReturnValue(Optional.of(returnValue));
        return Result.success();
    }
}
