package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that kicks a player out of the server
 */
public class KickStatement extends Statement {

    public KickStatement() {
        super(Syntax.builder()
                .identifiers("kick")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Player")), Arguments.of(Argument.from("Player"), ",", Argument.from("KickMessage")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        String message = ctx.getLiteral("KickMessage", "").getString();

        if (!player.isPresent()) {
            return Result.failure();
        }

        if (message.isEmpty()) {
            player.get().kick();
        } else {
            player.get().kick(Texts.of(message));
        }
        return Result.success();
    }
}
