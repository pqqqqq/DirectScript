package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.text.Texts;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that sends a a broadcast to the server
 */
public class BroadcastStatement extends SpongeStatement {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"broadcast"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Message").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        String message = ctx.getLiteral(0).getString();
        DirectScript.instance().getGame().getServer().getBroadcastSink().sendMessage(Texts.of(message));
        return Result.success();
    }
}
