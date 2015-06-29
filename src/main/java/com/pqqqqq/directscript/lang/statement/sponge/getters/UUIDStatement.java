package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-04.
 * A statement that retrieves the UUID of a player
 */
public class UUIDStatement extends Statement<String> {

    public UUIDStatement() {
        super(Syntax.builder()
                .identifiers("uuid")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Player")))
                .build());
    }

    @Override
    public Result<String> run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("Player").getPlayer();
        if (!player.isPresent()) {
            return Result.failure();
        }

        return Result.<String>builder().success().result(player.get().getIdentifier()).literal(player.get().getIdentifier()).build();
    }
}
