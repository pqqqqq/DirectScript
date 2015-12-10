package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Texts;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for players
 */
public class PlayerStatement extends Statement<Object> {

    public PlayerStatement() {
        super(Syntax.builder()
                .identifiers("player")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Object", Player.class).getAs(Player.class);
        if (!playerOptional.isPresent()) {
            return Result.builder().failure().result("Player object not found").build();
        }

        FoodData foodData = playerOptional.get().getFoodData();

        // Getters
        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(playerOptional.get().getName()).build();
        } else if (getter.equalsIgnoreCase("uuid")) {
            return Result.builder().success().result(playerOptional.get().getUniqueId()).build();
        } else if (getter.equalsIgnoreCase("ip")) {
            String address = playerOptional.get().getConnection().getAddress().getAddress().getHostAddress();
            return Result.builder().success().result(address).build();
        } else if (getter.equalsIgnoreCase("hand")) {
            ItemStack itemStack = playerOptional.get().getItemInHand().orElse(null);
            return Result.builder().success().result(itemStack).build();
        } else if (getter.equalsIgnoreCase("hunger") || getter.equalsIgnoreCase("food") || getter.equalsIgnoreCase("foodlevel")) {
            return Result.builder().success().result(foodData.foodLevel().get()).build();
        } else if (getter.equalsIgnoreCase("saturation")) {
            return Result.builder().success().result(foodData.saturation().get()).build();
        } else if (getter.equalsIgnoreCase("exhaustion")) {
            return Result.builder().success().result(foodData.exhaustion().get()).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Literal.Literals.EMPTY_ARRAY).getArray();
        if (getter.equalsIgnoreCase("permission") || getter.equalsIgnoreCase("haspermission") || getter.equalsIgnoreCase("perm")) {
            String permission = extraArguments.get(0).get().getString();
            return Result.builder().success().result(playerOptional.get().hasPermission(permission)).build();
        } else if (getter.equalsIgnoreCase("sethunger") || getter.equalsIgnoreCase("setfood") || getter.equalsIgnoreCase("setfoodlevel")) {
            int food = extraArguments.isEmpty() ? foodData.foodLevel().getMaxValue() : extraArguments.get(0).get().getNumber().intValue();
            playerOptional.get().offer(Keys.FOOD_LEVEL, food);
            return Result.success();
        } else if (getter.equalsIgnoreCase("kick")) {
            if (extraArguments.isEmpty()) {
                playerOptional.get().kick();
            } else {
                playerOptional.get().kick(Texts.of(extraArguments.get(0).get().getString()));
            }
            return Result.success();
        } else if (getter.equalsIgnoreCase("closeinventory") || getter.equalsIgnoreCase("closeinv")) {
            playerOptional.get().closeInventory();
            return Result.success();
        } else if (getter.equalsIgnoreCase("command")) {
            String command = extraArguments.get(0).get().getString();
            DirectScript.instance().getGame().getCommandManager().process(playerOptional.get(), command);
            return Result.success();
        } else if (getter.equalsIgnoreCase("give")) {
            Optional<ItemStack> itemStack = extraArguments.get(0).get().getAs(ItemStack.class);
            if (itemStack.isPresent()) {
                playerOptional.get().getInventory().offer(itemStack.get());
            } else {
                return Result.builder().failure().result("Unknown item stack").build();
            }
        }

        return Result.builder().failure().result("Unknown getter: " + getter).build();
    }
}
