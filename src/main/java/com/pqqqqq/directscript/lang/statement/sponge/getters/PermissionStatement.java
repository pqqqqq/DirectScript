package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that checks the permission of a player
 */
public class PermissionStatement extends Statement<Boolean> {

    public PermissionStatement() {
        super(Syntax.builder()
                .identifiers("permission")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Permission")), Arguments.of(Argument.from("Player"), ",", Argument.from("Permission")))
                .build());
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Optional<Player> player = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        String permission = ctx.getLiteral("Permission").getString();

        if (!player.isPresent()) {
            return Result.failure();
        }

        boolean result = player.get().hasPermission(permission);
        return Result.<Boolean>builder().success().result(result).build();
    }
}
