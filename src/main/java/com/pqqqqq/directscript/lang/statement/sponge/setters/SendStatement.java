package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that sends a message to a player
 */
public class SendStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("send", "tell")
            .prefix("@")
            .arguments(Arguments.of(GenericArguments.withName("Message")), Arguments.of(GenericArguments.withName("Player"), ",", GenericArguments.withName("Message")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        String message = ctx.getLiteral("Message").getString();

        if (!player.isPresent()) {
            return Result.builder().failure().error("No player found").build();
        }

        player.get().sendMessage(Utilities.getText(message));
        return Result.success();
    }
}
