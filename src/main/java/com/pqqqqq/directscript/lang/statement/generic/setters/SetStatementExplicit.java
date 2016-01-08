package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-12-10.
 * Explicitly sets a variable's value
 */
public class SetStatementExplicit extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("set")
            .arguments(Arguments.of(GenericArguments.withName("VariableName"), "=", GenericArguments.withName("Value")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        String variableName = ctx.getLiteral("VariableName").getString();
        Literal value = ctx.getLiteral("Value");

        Variable variable = ctx.getScriptInstance().getOrCreate(variableName);
        variable.setDatum(value);

        return Result.builder().success().result(value).build();
    }
}
