package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.Living;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for players
 */
public class LivingStatement extends Statement<Object> {

    public LivingStatement() {
        super(Syntax.builder()
                .identifiers("living")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Living> livingOptional = ctx.getLiteral("Object", Living.class).getAs(Living.class);
        if (!livingOptional.isPresent()) {
            return Result.builder().failure().result("Living object not found").build();
        }

        HealthData healthData = livingOptional.get().getHealthData();
        //DamageableData mortalData = livingOptional.get().getMortalData();

        // Getters
        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("health")) {
            return Result.builder().success().result(healthData.health().get()).build();
        } else if (getter.equalsIgnoreCase("maxhealth")) {
            return Result.builder().success().result(healthData.maxHealth().get()).build();
        } else if (getter.equalsIgnoreCase("lastattacker")) {
            //return Result.builder().success().result(mortalData.lastAttacker().get().orElse(null)).build();
        } else if (getter.equalsIgnoreCase("lastdamage")) {
            //return Result.builder().success().result(mortalData.lastDamage().get().orElse(-1D)).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Literal.Literals.EMPTY_ARRAY).getArray();
        if (getter.equalsIgnoreCase("sethealth")) {
            double health = extraArguments.isEmpty() ? healthData.maxHealth().get() : extraArguments.get(0).get().getNumber();
            livingOptional.get().offer(Keys.HEALTH, health);
            return Result.success();
        } else if (getter.equalsIgnoreCase("setmaxhealth")) {
            livingOptional.get().offer(Keys.MAX_HEALTH, extraArguments.get(0).get().getNumber());
            return Result.success();
        }

        return Result.builder().failure().result("Unknown getter: " + getter).build();
    }
}
