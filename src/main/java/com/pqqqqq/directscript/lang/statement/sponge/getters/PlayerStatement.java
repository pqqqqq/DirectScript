package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_PLAYER;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for players
 */
public class PlayerStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("player")
            .prefix("@")
            .build();

    public PlayerStatement() {
        super();
        inherit(Statements.LIVING); // Inherit living
        inherit(Statements.SOURCE); // Inherit command source

        register(this.<Player, Object>createCompartment(new String[]{"closeinventory", "closeinv"}, (ctx, player) -> {
            player.closeInventory();
            return Result.success();
        }));

        final Arguments[] GET_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<Player, String>createCompartment("name", (ctx, player) -> {
            return Result.<String>builder().success().result(player.getName()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, UUID>createCompartment("uuid", (ctx, player) -> {
            return Result.<UUID>builder().success().result(player.getUniqueId()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, String>createCompartment("ip", (ctx, player) -> {
            return Result.<String>builder().success().result(player.getConnection().getAddress().getAddress().getHostAddress()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, ItemStack>createCompartment("hand", (ctx, player) -> {
            return Result.<ItemStack>builder().success().result(player.getItemInHand().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Player, ItemStack>createCompartment("helmet", (ctx, player) -> {
            return Result.<ItemStack>builder().success().result(player.getHelmet().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Player, ItemStack>createCompartment(new String[]{"chest", "chestplate"}, (ctx, player) -> {
            return Result.<ItemStack>builder().success().result(player.getChestplate().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Player, ItemStack>createCompartment(new String[]{"pants", "legs", "leggings"}, (ctx, player) -> {
            return Result.<ItemStack>builder().success().result(player.getLeggings().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Player, ItemStack>createCompartment(new String[]{"boots", "feet"}, (ctx, player) -> {
            return Result.<ItemStack>builder().success().result(player.getBoots().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Integer>createCompartment(new String[]{"hunger", "food", "foodlevel"}, (ctx, player) -> {
            return Result.<Integer>builder().success().result(player.getFoodData().foodLevel().get()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Double>createCompartment("saturation", (ctx, player) -> {
            return Result.<Double>builder().success().result(player.getFoodData().saturation().get()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Double>createCompartment("exhaustion", (ctx, player) -> {
            return Result.<Double>builder().success().result(player.getFoodData().exhaustion().get()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Integer>createCompartment(new String[]{"level", "lvl", "explevel", "explvl", "experiencelevel", "experiencelvl"}, (ctx, player) -> {
            return Result.<Integer>builder().success().result(player.get(Keys.EXPERIENCE_LEVEL).get()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Integer>createCompartment(new String[]{"totalexp", "totalexperience", "exp", "experience"}, (ctx, player) -> {
            return Result.<Integer>builder().success().result(player.get(Keys.TOTAL_EXPERIENCE).get()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Integer>createCompartment(new String[]{"expsincelevel", "experiencesincelevel", "expsincelvl", "experiencesincelvl"}, (ctx, player) -> {
            return Result.<Integer>builder().success().result(player.get(Keys.EXPERIENCE_SINCE_LEVEL).get()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Integer>createCompartment(new String[]{"expbetweenlevel", "experiencebetweenlevel", "expbetweenlvl", "experiencebetweenlvl"}, (ctx, player) -> {
            return Result.<Integer>builder().success().result(player.get(Keys.EXPERIENCE_FROM_START_OF_LEVEL).get()).build();
        }, GET_ARGUMENTS));

        register(this.<Player, Boolean>createCompartment(new String[]{"permission", "haspermission", "perm", "hasperm"}, (ctx, player) -> {
            return Result.<Boolean>builder().success().result(player.hasPermission(ctx.getLiteral("Permission").getString())).build();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Permission"))));

        register(this.<Player, Object>createCompartment(new String[]{"sethunger", "setfood", "setfoodlevel"}, (ctx, player) -> {
            int food = ctx.getLiteral("Hunger", 20).getNumber().intValue();
            player.offer(Keys.FOOD_LEVEL, food);
            return Result.success();
        }, GenericArguments.requiredArguments(this, (Argument) null, GenericArguments.withName("Hunger"))));

        register(this.<Player, Object>createCompartment("setexhaustion", (ctx, player) -> {
            double exhaustion = ctx.getLiteral("Exhaustion", 20).getNumber();
            player.offer(Keys.EXHAUSTION, exhaustion);
            return Result.success();
        }, GenericArguments.requiredArguments(this, (Argument) null, GenericArguments.withName("Exhaustion"))));

        register(this.<Player, Object>createCompartment("setsaturation", (ctx, player) -> {
            double saturation = ctx.getLiteral("Saturation", 20).getNumber();
            player.offer(Keys.SATURATION, saturation);
            return Result.success();
        }, GenericArguments.requiredArguments(this, (Argument) null, GenericArguments.withName("Saturation"))));

        register(this.<Player, Object>createCompartment("kick", (ctx, player) -> {
            Literal message = ctx.getLiteral("Message");
            if (message.isEmpty()) {
                player.kick();
            } else {
                player.kick(Utilities.getText(message.getString()));
            }

            return Result.success();
        }, GenericArguments.requiredArguments(this, (Argument) null, GenericArguments.withName("Message"))));

        register(this.<Player, Boolean>createCompartment("command", (ctx, player) -> {
            String command = ctx.getLiteral("Command").getString();
            CommandResult result = DirectScript.instance().getGame().getCommandManager().process(player, command);
            return Result.<Boolean>builder().success().result(result.getSuccessCount().isPresent() && result.getSuccessCount().get() > 0).build();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Command"))));

        register(this.<Player, Object>createCompartment("give", (ctx, player) -> {
            Optional<ItemStack> itemStack = ctx.getLiteral("ItemStack", ItemStack.class).getAs(ItemStack.class);
            if (itemStack.isPresent()) {
                player.getInventory().offer(itemStack.get());
                return Result.success();
            } else {
                return Result.builder().failure().error("Unknown item stack").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.itemStack("ItemStack", 0, null))));

        register(this.<Player, Object>createCompartment("take", (ctx, player) -> {
            Optional<ItemStack> itemStack = ctx.getLiteral("ItemStack", ItemStack.class).getAs(ItemStack.class);
            if (itemStack.isPresent()) {
                int amount = ctx.getLiteral("Amount", 1).getNumber().intValue();
                player.getInventory().query(itemStack.get()).poll(amount);
                return Result.success();
            } else {
                return Result.builder().failure().error("Unknown item type").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.itemStack("ItemStack", 0, null), GenericArguments.withName("Amount"))));

        register(this.<Player, Object>createCompartment(new String[]{"setlevel", "setlvl", "setexplevel", "setexplvl", "setexperiencelevel", "setexperiencelvl"}, (ctx, player) -> {
            player.offer(Keys.EXPERIENCE_LEVEL, ctx.getLiteral("Level").getNumber().intValue());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Level"))));

        register(this.<Player, Object>createCompartment(new String[]{"setexp", "setexperience", "settotalexp", "settotalexperience"}, (ctx, player) -> {
            player.offer(Keys.TOTAL_EXPERIENCE, ctx.getLiteral("Exp").getNumber().intValue());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Exp"))));

        register(this.<Player, Object>createCompartment(new String[]{"setexperiencesincelevel", "setexpsincelevel", "setexperiencesincelvl", "setexpsincelvl"}, (ctx, player) -> {
            player.offer(Keys.EXPERIENCE_SINCE_LEVEL, ctx.getLiteral("Exp").getNumber().intValue());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Exp"))));

        register(this.<Player, Object>createCompartment("sethand", (ctx, player) -> {
            Optional<ItemStack> itemStack = ctx.getLiteral("ItemStack", ItemStack.class).getAs(ItemStack.class);
            if (itemStack.isPresent()) {
                player.setItemInHand(itemStack.get());
                return Result.success();
            } else {
                return Result.builder().failure().error("Unknown item stack").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.itemStack("ItemStack", 0, null))));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_PLAYER;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
