package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-14.
 * A statement that creates an array of a repeated literal
 */
public class RepeatStatement extends Statement<Object[]> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("repeat")
            .arguments(Arguments.of(GenericArguments.withName("Literal"), ",", GenericArguments.withName("Amount")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Object[]> run(Context ctx) {
        Object repeatObj = ctx.getLiteral("Literal").getValue().get();
        int repeatTimes = ctx.getLiteral("Amount").getNumber().intValue();

        Object[] array = new Object[repeatTimes];
        for (int i = 0; i < array.length; i++) {
            array[i] = repeatObj;
        }

        return Result.<Object[]>builder().success().result(array).build();
    }
}
