package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that takes the substring of a literal
 */
public class SubstringStatement extends Statement<String> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"substring"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build(),
                Argument.builder().name("Start").build(),
                Argument.builder().name("End").optional().build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        String string = ctx.getLiteral(0).getString();
        int start = ctx.getLiteral(1).getNumber().intValue();
        int end = ctx.getLiteral(2, string.length()).getNumber().intValue();
        String sub = string.substring(start, end);

        return Result.<String>builder().success().result(sub).literal(sub).build();
    }
}
