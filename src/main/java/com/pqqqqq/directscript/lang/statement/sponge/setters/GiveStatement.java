package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by Kevin on 2015-06-30.
 * A statement that gives an item to a player
 */
@Statement.Concept
public class GiveStatement extends Statement {

    public GiveStatement() {
        super(Syntax.builder()
                .identifiers("give")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("ItemStack")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("ItemStack")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        Optional<ItemStack> itemStackOptional = ctx.getLiteral("ItemStack", ItemStack.class).getAs(ItemStack.class);
        if (!itemStackOptional.isPresent()) {
            return Result.failure();
        }

        playerOptional.get().getInventory().offer(itemStackOptional.get());
        return Result.success();
    }
}
