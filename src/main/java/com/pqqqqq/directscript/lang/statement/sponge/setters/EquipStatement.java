package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Kevin on 2015-07-20.
 * A statement that equips certain items on a player (TODO ArmorEquipable instead?)
 */
public class EquipStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("equip")
            .prefix("@")
            .arguments(Arguments.of(GenericArguments.withName("ArmourSpot")))
            .arguments(Arguments.of(GenericArguments.withName("ArmourSpot"), ",", GenericArguments.withName("ItemStack")))
            .arguments(Arguments.of(GenericArguments.withName("Player"), ",", GenericArguments.withName("ArmourSpot"), ",", GenericArguments.withName("ItemStack")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        String armourSpot = ctx.getLiteral("ArmourSpot").getString();
        ItemStack itemStack = ((Optional<ItemStack>) ctx.getLiteral("ItemStack", ItemStack.class).getAs(ItemStack.class)).orElse(null);

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
