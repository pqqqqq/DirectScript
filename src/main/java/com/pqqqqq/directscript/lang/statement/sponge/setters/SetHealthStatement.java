package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.manipulator.entity.HealthData;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by Kevin on 2015-06-22.
 * A statement that sets the health status for a player
 */
public class SetHealthStatement extends Statement {

    public SetHealthStatement() {
        super(Syntax.builder()
                .identifiers("sethealth")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Health")), Arguments.of(Argument.from("Living"), ",", Argument.from("Health")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Living> livingOptional = ctx.getLiteral("Living", Living.class).getAs(Living.class);
        if (!livingOptional.isPresent()) {
            return Result.failure();
        }

        HealthData healthData = livingOptional.get().getHealthData();
        double health = ctx.getLiteral("Health", healthData.getMaxHealth()).getNumber();

        healthData.setHealth(health);
        livingOptional.get().offer(healthData);
        return Result.success();
    }
}
