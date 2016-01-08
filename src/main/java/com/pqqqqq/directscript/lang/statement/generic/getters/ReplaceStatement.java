package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that replaces all entries in a literal
 */
public class ReplaceStatement extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("replace")
            .arguments(Arguments.of(GenericArguments.withName("String"), ",", GenericArguments.withName("FindString"), ",", GenericArguments.withName("Replacement")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        String string = ctx.getLiteral("String").getString();
        String find = ctx.getLiteral("FindString").getString();
        String replace = ctx.getLiteral("Replacement").getString();
        String result = string.replace(find, replace);

        return Result.<String>builder().success().result(result).build();
    }
}
