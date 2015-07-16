package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Collection;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that checks if a string contains another string
 */
public class ContainsStatement extends Statement<Boolean> {

    public ContainsStatement() {
        super(Syntax.builder()
                .identifiers("contains")
                .arguments(Arguments.of(Argument.from("Container"), ",", Argument.from("Check")))
                .build());
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Literal container = ctx.getLiteral("Container");

        boolean result = false;
        if (container.isArray() || container.isMap()) {
            Collection<LiteralHolder> array = (container.isArray() ? container.getArray() : container.getMap().keySet());
            Literal check = ctx.getLiteral("Check");

            for (LiteralHolder literalHolder : array) {
                if (literalHolder.getData().equals(check)) {
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
