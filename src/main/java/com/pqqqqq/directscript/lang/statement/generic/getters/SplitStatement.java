package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that splits a string into an array by a certain split string
 */
public class SplitStatement extends Statement<String[]> {

    public SplitStatement() {
        super(Syntax.builder()
                .identifiers("split")
                .arguments(Arguments.of(Argument.from("String"), ",", Argument.from("SplitString")))
                .build());
    }

    @Override
    public Result<String[]> run(Context ctx) {
        String[] split = ctx.getLiteral("String").getString().split(ctx.getLiteral("SplitString").getString());
        return Result.<String[]>builder().success().result(split).build();
    }
}
