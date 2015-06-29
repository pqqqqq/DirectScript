package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that runs a command from the player
 */
public class PlayerCommandStatement extends Statement {

    public PlayerCommandStatement() {
        super(Syntax.builder()
                .identifiers("command") // This differs from CommandStatement by the prefix
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Command")), Arguments.of(Argument.from("Player"), ",", Argument.from("Command")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player").getPlayer();
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        String command = ctx.getLiteral("Command").getString();
        DirectScript.instance().getGame().getCommandDispatcher().process(playerOptional.get(), command);
        return Result.success();
    }
}
