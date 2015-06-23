package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that gets the size of an array
 */
public class SizeStatement extends Statement<Integer> {

    public SizeStatement() {
        super(Syntax.builder()
                .identifiers("size")
                .arguments(Arguments.of(Argument.from("Array")))
                .build());
    }

    @Override
    public Result<Integer> run(Context ctx) {
        int size = ctx.getLiteral("Array").getArray().size();
        return Result.<Integer>builder().success().result(size).literal(size).build();
    }
}
