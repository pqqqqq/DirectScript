package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.manipulator.entity.FoodData;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that gets the hunger level of a player
 */
public class HungerStatement extends Statement<Double> {

    public HungerStatement() {
        super(Syntax.builder()
                .identifiers("hunger")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Player")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        Optional<Player> playerOptional = ctx.getLiteral("Player").getPlayer();
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        FoodData foodData = playerOptional.get().getData(FoodData.class).get();
        double foodLevel = foodData.getFoodLevel();
        return Result.<Double>builder().success().result(foodLevel).literal(foodLevel).build();
    }
}
