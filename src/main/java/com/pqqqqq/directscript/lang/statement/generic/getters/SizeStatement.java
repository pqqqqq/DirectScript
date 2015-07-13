package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.data.Literal;
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
        Literal array = ctx.getLiteral("Array");
        int size = array.isArray() ? array.getArray().size() : array.getMap().size();
        return Result.<Integer>builder().success().result(size).literal(size).build();
    }
}
