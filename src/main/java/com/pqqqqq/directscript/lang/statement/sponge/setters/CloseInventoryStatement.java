package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that closes a player's inventory
 */
public class CloseInventoryStatement extends Statement {

    public CloseInventoryStatement() {
        super(Syntax.builder()
                .identifiers("closeinventory", "closeinv")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Player")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        playerOptional.get().closeInventory();
        return Result.success();
    }
}
