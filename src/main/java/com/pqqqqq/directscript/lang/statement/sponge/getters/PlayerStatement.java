package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.manipulator.entity.FoodData;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for players
 */
public class PlayerStatement extends Statement<Object> {

    public PlayerStatement() {
        super(Syntax.builder()
                .identifiers("player")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Getter"), ",", Argument.from("Player")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player", Player.class).getAs(Player.class);
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(playerOptional.get().getName()).literal(playerOptional.get().getName()).build();
        } else if (getter.equalsIgnoreCase("uuid")) {
            return Result.builder().success().result(playerOptional.get().getIdentifier()).literal(playerOptional.get().getIdentifier()).build();
        } else if (getter.equalsIgnoreCase("ip")) {
            String address = playerOptional.get().getConnection().getAddress().getAddress().getHostAddress();
            return Result.builder().success().result(address).literal(address).build();
        } else if (getter.equalsIgnoreCase("hunger")) {
            FoodData foodData = playerOptional.get().getData(FoodData.class).get();
            double foodLevel = foodData.getFoodLevel();
            return Result.builder().success().result(foodLevel).literal(foodLevel).build();
        }

        return Result.failure();
    }
}
