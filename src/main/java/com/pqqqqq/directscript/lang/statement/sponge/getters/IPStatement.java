package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that retrieves a player's IP address
 */
public class IPStatement extends Statement<String> {

    public IPStatement() {
        super(Syntax.builder()
                .identifiers("ip")
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

        String ip = player.get().getConnection().getAddress().getAddress().getHostAddress();
        return Result.<String>builder().success().result(ip).literal(ip).build();
    }
}
