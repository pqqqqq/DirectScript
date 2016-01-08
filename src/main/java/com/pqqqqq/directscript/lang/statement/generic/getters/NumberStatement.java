package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-15.
 * A statement that returns the number value of a literal
 */
public class NumberStatement extends Statement<Double> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("number")
            .arguments(Arguments.of(GenericArguments.withName("Literal")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Double> run(Context ctx) {
        Double numberValue = ctx.getLiteral("Literal").getNumber();
        return Result.<Double>builder().success().result(numberValue).build();
    }
}
