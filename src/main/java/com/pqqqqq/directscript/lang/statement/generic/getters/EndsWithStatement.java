package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks if a string ends with another string
 */
public class EndsWithStatement extends Statement<Boolean> {

    public EndsWithStatement() {
        super(Syntax.builder()
                .identifiers("endswith")
                .arguments(Arguments.of(Argument.from("String"), ",", Argument.from("EndsWithString")))
                .build());
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        String string = ctx.getLiteral("String").getString();
        String contains = ctx.getLiteral("EndsWithString").getString();
        boolean check = string.endsWith(contains);

        return Result.<Boolean>builder().success().result(check).literal(check).build();
    }
}
