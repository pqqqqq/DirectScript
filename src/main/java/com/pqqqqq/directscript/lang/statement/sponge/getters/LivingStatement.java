package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.manipulator.entity.HealthData;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for players
 */
public class LivingStatement extends Statement<Object> {

    public LivingStatement() {
        super(Syntax.builder()
                .identifiers("living")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Getter"), ",", Argument.from("Living")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Living> livingOptional = ctx.getLiteral("Living", Living.class).getAs(Living.class);
        if (!livingOptional.isPresent()) {
            return Result.failure();
        }

        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("health")) {
            HealthData healthData = livingOptional.get().getHealthData();
            double health = healthData.getHealth();
            return Result.builder().success().result(health).build();
        }

        return Result.failure();
    }
}
