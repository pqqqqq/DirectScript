package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-04.
 * A statement that gets the name of the specified player
 */
public class NameStatement extends Statement<String> {

    public NameStatement() {
        super(Syntax.builder()
                .identifiers("playername")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Player")))
                .build());
    }

    @Override
    public Result<String> run(Context ctx) {
        Optional<Player> player = ctx.getPlayerOrCauser("Player");
        if (!player.isPresent()) {
            return Result.failure();
        }

        return Result.<String>builder().success().result(player.get().getName()).literal(player.get().getName()).build();
    }
}
