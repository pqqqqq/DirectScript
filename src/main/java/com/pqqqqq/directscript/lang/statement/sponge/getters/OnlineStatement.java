package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that checks if a player is online
 */
public class OnlineStatement extends Statement<Boolean> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("online")
            .prefix("@")
            .arguments(Arguments.of(GenericArguments.withName("User")))
            .build();

    @Override
    public Result<Boolean> run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("User").getAs(Player.class);
        return Result.<Boolean>builder().result(player.isPresent()).build();
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
