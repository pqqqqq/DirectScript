package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks where a string contains another string
 */
public class FindStatement extends Statement<Integer> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("find", "indexof")
            .arguments(Arguments.of(GenericArguments.withName("String"), ",", GenericArguments.withName("FindString")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Integer> run(Context ctx) {
        String string = ctx.getLiteral("String").getString();
        String contains = ctx.getLiteral("FindString").getString();
        int find = string.indexOf(contains) + 1; // Base 1

        return Result.<Integer>builder().success().result(find).build();
    }
}
