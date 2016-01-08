package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that floors (rounds down) a number
 */
public class FloorStatement extends Statement<Double> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("floor")
            .arguments(Arguments.of(GenericArguments.withName("Number")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Double> run(Context ctx) {
        double floor = Math.floor(ctx.getLiteral("Number").getNumber());
        return Result.<Double>builder().success().result(floor).build();
    }
}
