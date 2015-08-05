package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;

/**
 * Created by Kevin on 2015-07-23.
 * A statement that generates a random integer in bounds
 */
public class RandomIntStatement extends Statement<Integer> {

    public RandomIntStatement() {
        super(Syntax.builder()
                .identifiers("randomint", "randint")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Max")), Arguments.of(Argument.from("Min"), ",", Argument.from("Max")))
                .build());
    }

    @Override
    public Result<Integer> run(Context ctx) {
        int min = ctx.getLiteral("Min", Integer.MIN_VALUE).getNumber().intValue();
        int max = ctx.getLiteral("Max", Integer.MAX_VALUE).getNumber().intValue();

        return Result.<Integer>builder().success().result(Utilities.randomInt(min, max)).build();
    }
}
