package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

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
        boolean result = ctx.getLiteral("Condition").getBoolean();
        if (!result) {
            ctx.getScriptInstance().setSkipLines(true);
        }

        return Result.<Boolean>builder().success().result(result).build();
    }
}
