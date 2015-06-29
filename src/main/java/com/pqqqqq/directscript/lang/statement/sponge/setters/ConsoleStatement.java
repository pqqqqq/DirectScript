package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that runs a command from the server's console
 */
public class ConsoleStatement extends Statement {

    public ConsoleStatement() {
        super(Syntax.builder()
                .identifiers("console")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Command")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String command = ctx.getLiteral("Command").getString();
        DirectScript.instance().getGame().getCommandDispatcher().process(DirectScript.instance().getGame().getServer().getConsole(), command);
        return Result.success();
    }
}
