package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-08-04.
 * Gets the current Unix time in milliseconds
 */
public class MillisStatement extends Statement<Long> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("millis")
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Long> run(Context ctx) {
        return Result.<Long>builder().success().result(System.currentTimeMillis()).build();
    }
}
