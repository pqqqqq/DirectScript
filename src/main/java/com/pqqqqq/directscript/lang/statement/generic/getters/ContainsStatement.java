package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Collection;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks if a string contains another string
 */
public class ContainsStatement extends Statement<Boolean> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("contains")
            .arguments(Arguments.of(GenericArguments.withName("Container"), ",", GenericArguments.withName("Check")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Literal container = ctx.getLiteral("Container");

        boolean result = false;
        if (container.isArray() || container.isMap()) {
            Collection<Literal> array = (container.isArray() ? container.getArray() : container.getMap().keySet());
            Literal check = ctx.getLiteral("Check");

            for (Literal literal : array) {
                if (literal.getString().equals(check.getString())) {
                    result = true;
                    break;
                }
            }
        } else {
            String stringContainer = container.getString();
            String check = ctx.getLiteral("Check").getString();
            result = stringContainer.contains(check);
        }

        return Result.<Boolean>builder().success().result(result).build();
    }
}
