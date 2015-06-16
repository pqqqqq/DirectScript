package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks where a string contains another string
 */
public class FindStatement extends Statement<Integer> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"find", "indexof"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build(),
                Argument.builder().name("FindString").build()
        };
    }

    @Override
    public Result<Integer> run(Context ctx) {
        String string = ctx.getLiteral(0).getString();
        String contains = ctx.getLiteral(1).getString();
        int find = string.indexOf(contains);

        return Result.<Integer>builder().success().result(find).literal(find).build();
    }
}
