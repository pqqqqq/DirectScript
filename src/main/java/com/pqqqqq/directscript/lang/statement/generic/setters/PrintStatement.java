package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-02.
 */
public class PrintStatement extends Statement {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"print"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        DirectScript.instance().getLogger().info(ctx.getLiteral(0).getString());
        return Result.success();
    }
}
