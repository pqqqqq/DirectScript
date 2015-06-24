package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.manipulator.entity.HealthData;
import org.spongepowered.api.entity.player.Player;

/**
 * Created by Kevin on 2015-06-22.
 * A statement that retrieves the health status of a player
 */
public class HealthStatement extends Statement<Double> {

    public HealthStatement() {
        super(Syntax.builder()
                .identifiers("health")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Player")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        Optional<Player> playerOptional = ctx.getPlayerOrCauser("Player");
        if (!playerOptional.isPresent()) {
            return Result.failure();
        }

        HealthData healthData = playerOptional.get().getHealthData();
        double health = healthData.getHealth();
        return Result.<Double>builder().result(health).literal(health).build();
    }
}
