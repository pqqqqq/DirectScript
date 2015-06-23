package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-15.
 * A statement that returns the number value of a literal
 */
public class NumberStatement extends Statement<Double> {

    public NumberStatement() {
        super(Syntax.builder()
                .identifiers("number")
                .arguments(Arguments.of(Argument.from("Literal")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        Double numberValue = ctx.getLiteral("Literal").getNumber();
        return Result.<Double>builder().success().result(numberValue).literal(numberValue).build();
    }
}
