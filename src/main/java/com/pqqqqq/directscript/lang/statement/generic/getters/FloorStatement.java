package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-10.
 */
public class FloorStatement extends Statement<Double> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"floor"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Number").build()
        };
    }

    @Override
    public Result<Double> run(Context ctx) {
        double floor = Math.floor(ctx.getLiteral(0).getNumber());
        return Result.<Double>builder().success().result(floor).literal(floor).build();
    }
}
