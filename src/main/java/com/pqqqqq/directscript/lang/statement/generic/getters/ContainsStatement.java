package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks if a string contains another string
 */
public class ContainsStatement extends Statement<Boolean> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"contains"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build(),
                Argument.builder().name("ContainsString").build()
        };
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        String string = ctx.getLiteral(0).getString();
        String contains = ctx.getLiteral(1).getString();
        boolean check = string.contains(contains);

        return Result.<Boolean>builder().success().result(check).literal(check).build();
    }
}
