package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * Trims a string througb {@link String#trim()}
 */
public class TrimStatement extends Statement<String> {

    public TrimStatement() {
        super(Syntax.builder()
                .identifiers("trim")
                .arguments(Arguments.of(Argument.from("String")))
                .build());
    }

    @Override
    public Result<String> run(Context ctx) {
        String trim = ctx.getLiteral("String").getString().trim();
        return Result.<String>builder().success().result(trim).literal(trim).build();
    }
}
