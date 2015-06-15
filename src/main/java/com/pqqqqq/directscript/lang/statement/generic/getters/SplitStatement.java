package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that splits a string into an array by a certain split string
 */
public class SplitStatement extends Statement<String[]> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"split"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build(),
                Argument.builder().name("SplitString").build()
        };
    }

    @Override
    public Result<String[]> run(Context ctx) {
        String[] split = ctx.getLiteral(0).getString().split(ctx.getLiteral(1).getString());
        return Result.<String[]>builder().success().result(split).literal(split).build();
    }
}
