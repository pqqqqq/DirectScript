package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-15.
 * A statement that returns the number value of a literal
 */
public class NumberStatement extends Statement<Double> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"number"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Literal").build()
        };
    }

    @Override
    public Result<Double> run(Context ctx) {
        Double numberValue = ctx.getLiteral(0).getNumber();
        return Result.<Double>builder().success().result(numberValue).literal(numberValue).build();
    }
}
