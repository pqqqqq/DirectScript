package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that sends a a broadcast to the server
 */
public class BroadcastStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("broadcast")
            .prefix("@")
            .arguments(Arguments.of(GenericArguments.withName("Message")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        String message = ctx.getLiteral("Message").getString();
        DirectScript.instance().getGame().getServer().getBroadcastChannel().send(Utilities.getText(message));
        return Result.success();
    }
}
