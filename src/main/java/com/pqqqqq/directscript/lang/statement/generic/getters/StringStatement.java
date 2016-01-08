package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-15.
 * A statement that returns the string value of a literal
 */
public class StringStatement extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("string")
            .arguments(Arguments.of(GenericArguments.withName("Literal")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        String stringValue = ctx.getLiteral("Literal").getString();
        return Result.<String>builder().success().result(stringValue).build();
    }
}
