package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that turns all letters in a literal to lowercase
 */
public class LowercaseStatement extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("lowercase")
            .arguments(Arguments.of(GenericArguments.withName("String")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        String lower = ctx.getLiteral("String").getString().toLowerCase();
        return Result.<String>builder().success().result(lower).build();
    }
}
