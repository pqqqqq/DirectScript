package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-15.
 * A statement that returns the boolean value of a literal
 */
public class BooleanStatement extends Statement<Boolean> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"boolean", "bool"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Literal").build()
        };
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Boolean booleanValue = ctx.getLiteral(0).getBoolean();
        return Result.<Boolean>builder().success().result(booleanValue).literal(booleanValue).build();
    }
}
