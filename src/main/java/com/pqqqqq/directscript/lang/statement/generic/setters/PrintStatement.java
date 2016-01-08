package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that prints to console
 */
public class PrintStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("print")
            .arguments(Arguments.empty(), Arguments.of(GenericArguments.withName("String")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        DirectScript.instance().getLogger().info(ctx.getLiteral("String", "").getString());
        return Result.success();
    }
}
