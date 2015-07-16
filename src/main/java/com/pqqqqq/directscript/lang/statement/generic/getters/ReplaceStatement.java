package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that replaces all entries in a literal
 */
public class ReplaceStatement extends Statement<String> {

    public ReplaceStatement() {
        super(Syntax.builder()
                .identifiers("replace")
                .arguments(Arguments.of(Argument.from("String"), ",", Argument.from("FindString"), ",", Argument.from("Replacement")))
                .build());
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
