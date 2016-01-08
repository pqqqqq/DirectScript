package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_LIVING;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for players
 */
public class LivingStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("living")
            .prefix("@")
            .build();

    public LivingStatement() {
        super();

        final Arguments[] GET_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<Living, Double>createCompartment("health", (ctx, living) -> {
            return Result.<Double>builder().success().result(living.getHealthData().health().get()).build();
        }, GET_ARGUMENTS));

        register(this.<Living, Double>createCompartment("maxhealth", (ctx, living) -> {
            return Result.<Double>builder().success().result(living.getHealthData().maxHealth().get()).build();
        }, GET_ARGUMENTS));

        register(this.<Living, Living>createCompartment("lastattacker", (ctx, living) -> {
            return Result.<Living>builder().success().result(living.getMortalData().lastAttacker().get().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Living, Double>createCompartment("lastdamage", (ctx, living) -> {
            return Result.<Double>builder().success().result(living.getMortalData().lastDamage().get().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Living, Object>createCompartment("sethealth", (ctx, living) -> {
            double health = ctx.getLiteral("Health", living.getHealthData().maxHealth().get()).getNumber();
            living.offer(Keys.HEALTH, health);
            return Result.success();
        }, GenericArguments.requiredArguments(this, (Argument) null, GenericArguments.withName("Health"))));

        register(this.<Living, Object>createCompartment("setmaxhealth", (ctx, living) -> {
            double health = ctx.getLiteral("Health", living.getHealthData().maxHealth().get()).getNumber();
            living.offer(Keys.MAX_HEALTH, health);
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Health"))));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_LIVING;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
