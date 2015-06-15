package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that sends a message to a player
 */
public class PlayerStatement extends SpongeStatement {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"player"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Player").optional().build(),
                Argument.builder().name("Message").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> player = ctx.getPlayerOrCauser(0);
        String message = ctx.getLiteral(1).getString();

        if (!player.isPresent()) {
            return Result.failure();
        }

        player.get().sendMessage(Texts.of(message));
        return Result.success();
    }
}
