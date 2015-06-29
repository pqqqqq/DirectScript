package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.manipulator.entity.FoodData;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that sets the hunger level of a player
 */
@Statement.Concept
public class SetHungerStatement extends Statement {

    public SetHungerStatement() {
        super(Syntax.builder()
                .identifiers("sethunger")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Hunger")), Arguments.of(Argument.from("Player"), ",", Argument.from("Hunger")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player").getPlayer();
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        FoodData foodData = playerOptional.get().getData(FoodData.class).get();
        double foodLevel = ctx.getLiteral("Hunger", 20D).getNumber(); // 20 is max food level

        foodData.setFoodLevel(foodLevel);
        playerOptional.get().offer(foodData);
        return Result.success();
    }
}
