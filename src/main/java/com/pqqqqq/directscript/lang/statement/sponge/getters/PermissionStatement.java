package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.sponge.SpongeStatement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that checks the permission of a player
 */
public class PermissionStatement extends SpongeStatement<Boolean> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"permission"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Player").optional().build(),
                Argument.builder().name("Permission").build()
        };
    }

    @Override
    public Result<Boolean> run(Context ctx) {
        Optional<Player> player = ctx.getPlayerOrCauser(0);
        String permission = ctx.getLiteral(1).getString();

        if (!player.isPresent()) {
            return Result.failure();
        }

        boolean result = player.get().hasPermission(permission);
        return Result.<Boolean>builder().success().result(result).literal(result).build();
    }
}
