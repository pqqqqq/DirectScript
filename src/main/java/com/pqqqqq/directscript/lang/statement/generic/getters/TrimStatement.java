package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * Trims a string througb {@link String#trim()}
 */
public class TrimStatement extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("trim")
            .arguments(Arguments.of(GenericArguments.withName("String")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        String trim = ctx.getLiteral("String").getString().trim();
        return Result.<String>builder().success().result(trim).build();
    }
}
