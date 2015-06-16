package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that turns all letters in a literal to lowercase
 */
public class LowercaseStatement extends Statement<String> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"lowercase"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        String lower = ctx.getLiteral(0).getString().toLowerCase();
        return Result.<String>builder().result(lower).literal(lower).build();
    }
}
