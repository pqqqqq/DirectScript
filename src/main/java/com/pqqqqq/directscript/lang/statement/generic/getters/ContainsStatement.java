package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks if a string contains another string
 */
public class ContainsStatement extends Statement<Boolean> {

    public ContainsStatement() {
        super(Syntax.builder()
                .identifiers("contains")
                .arguments(Arguments.of(Argument.from("String"), ",", Argument.from("ContainsString")))
                .build());
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        String string = ctx.getLiteral("String").getString();
        String contains = ctx.getLiteral("ContainsString").getString();
        boolean check = string.contains(contains);

        return Result.<Boolean>builder().success().result(check).literal(check).build();
    }
}
