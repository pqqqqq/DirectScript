package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-09.
 * Trims a string througb {@link String#trim()}
 */
public class TrimStatement extends Statement<String> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"trim"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        String trim = ctx.getLiteral(0).getString().trim();
        return Result.<String>builder().result(trim).literal(trim).build();
    }
}
