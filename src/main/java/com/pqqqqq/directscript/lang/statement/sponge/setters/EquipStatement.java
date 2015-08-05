package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by Kevin on 2015-07-20.
 * A statement that equips certain items on a player (TODO ArmorEquipable instead?)
 */
public class EquipStatement extends Statement {

    public EquipStatement() {
        super(Syntax.builder()
                .identifiers("equip")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("ArmourSpot")))
                .arguments(Arguments.of(Argument.from("ArmourSpot"), ",", Argument.from("ItemStack")))
                .arguments(Arguments.of(Argument.from("Player"), ",", Argument.from("ArmourSpot"), ",", Argument.from("ItemStack")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        String armourSpot = ctx.getLiteral("ArmourSpot").getString();
        ItemStack itemStack = ((Optional<ItemStack>) ctx.getLiteral("ItemStack", ItemStack.class).getAs(ItemStack.class)).orNull();

        if (armourSpot.equalsIgnoreCase("hand")) {
            playerOptional.get().setItemInHand(itemStack);
            return Result.success();
        } else if (armourSpot.equalsIgnoreCase("helmet")) {
            playerOptional.get().setHelmet(itemStack);
            return Result.success();
        } else if (armourSpot.equalsIgnoreCase("chestplate") || armourSpot.equalsIgnoreCase("chest")) {
            playerOptional.get().setChestplate(itemStack);
            return Result.success();
        } else if (armourSpot.equalsIgnoreCase("legs") || armourSpot.equalsIgnoreCase("leggings")) {
            playerOptional.get().setLeggings(itemStack);
            return Result.success();
        } else if (armourSpot.equalsIgnoreCase("boots")) {
            playerOptional.get().setBoots(itemStack);
            return Result.success();
        }

        return Result.failure();
    }
}
