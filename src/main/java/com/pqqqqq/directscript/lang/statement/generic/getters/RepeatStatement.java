package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-14.
 * A statement that creates an array of a repeated literal
 */
public class RepeatStatement extends Statement<Object[]> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"repeat"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("RepeatLiteral").build(),
                Argument.builder().name("RepeatTimes").build()
        };
    }

    @Override
    public Result<Object[]> run(Context ctx) {
        Object repeatObj = ctx.getLiteral(0).getValue().get();
        int repeatTimes = ctx.getLiteral(1).getNumber().intValue();

        Object[] array = new Object[repeatTimes];
        for (int i = 0; i < array.length; i++) {
            array[i] = repeatObj;
        }

        return Result.<Object[]>builder().success().result(array).literal(array).build();
    }
}
