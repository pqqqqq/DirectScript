package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.text.Texts;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that sends a a broadcast to the server
 */
public class BroadcastStatement extends Statement {

    public BroadcastStatement() {
        super(Syntax.builder()
                .identifiers("broadcast")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Message")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String message = ctx.getLiteral("Message").getString();
        DirectScript.instance().getGame().getServer().getBroadcastSink().sendMessage(Texts.of(message));
        return Result.success();
    }
}
