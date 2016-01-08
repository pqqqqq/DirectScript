package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-12-28.
 * Gets the absolute value of the number
 */
public class AbsStatement extends Statement<Double> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("abs", "absolute")
            .arguments(Arguments.of(GenericArguments.withName("Number")))
            .build();

    @Override
    public Result<Double> run(Context ctx) {
        return Result.<Double>builder().success().result(Math.abs(ctx.getLiteral("Number").getNumber())).build();
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
