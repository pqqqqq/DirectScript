package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks where a string contains another string
 */
public class FindStatement extends Statement<Integer> {

    public FindStatement() {
        super(Syntax.builder()
                .identifiers("find", "indexof")
                .arguments(Arguments.of(Argument.from("String"), ",", Argument.from("FindString")))
                .build());
    }

    @Override
    public Result<Integer> run(Context ctx) {
        String string = ctx.getLiteral("String").getString();
        String contains = ctx.getLiteral("FindString").getString();
        int find = string.indexOf(contains);

        return Result.<Integer>builder().success().result(find).literal(find).build();
    }
}
