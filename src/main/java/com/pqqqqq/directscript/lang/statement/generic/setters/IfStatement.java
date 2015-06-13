package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-04.
 */
public class IfStatement extends Statement<Boolean> {

    @Override
    public String getSuffix() {
        return "{";
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"if"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Condition").build()
        };
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        boolean result = ctx.getLiteral(0).getBoolean();
        if (!result) {
            ctx.getScriptInstance().setSkipLines(true);
        }

        return Result.<Boolean>builder().success().result(result).build();
    }
}
