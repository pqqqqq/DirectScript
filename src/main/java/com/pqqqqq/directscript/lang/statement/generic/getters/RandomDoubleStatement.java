package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;

/**
 * Created by Kevin on 2015-07-23.
 * A statement that generates a random double in bounds
 */
public class RandomDoubleStatement extends Statement<Double> {

    public RandomDoubleStatement() {
        super(Syntax.builder()
                .identifiers("randomdouble", "randdouble")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Max")), Arguments.of(Argument.from("Min"), ",", Argument.from("Max")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        double min = ctx.getLiteral("Min", Integer.MIN_VALUE).getNumber();
        double max = ctx.getLiteral("Max", Integer.MAX_VALUE).getNumber();

        return Result.<Double>builder().success().result(Utilities.randomDouble(min, max)).build();
    }
}
