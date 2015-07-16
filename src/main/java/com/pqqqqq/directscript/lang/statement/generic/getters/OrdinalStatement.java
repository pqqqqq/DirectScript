package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that uses the first character of a literal to find its ASCII ordinal
 */
public class OrdinalStatement extends Statement<Integer> {

    public OrdinalStatement() {
        super(Syntax.builder()
                .identifiers("ordinal", "ord")
                .arguments(Arguments.of(Argument.from("String")))
                .build());
    }

    @Override
    public Result<Integer> run(Context ctx) {
        String string = ctx.getLiteral("String").getString();
        char firstChar = string.charAt(0);
        int ordinal = (int) firstChar;

        return Result.<Integer>builder().success().result(ordinal).build();
    }
}
