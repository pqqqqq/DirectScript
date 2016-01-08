package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.collect.Sets;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.DamageModifier;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierType;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Kevin on 2015-12-29.
 * A statement to modify sponge events
 */
public class EventStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("event")
            .prefix("@")
            .build();

    public EventStatement() {
        super();

        register(this.<Object, Boolean>createCompartment("cancel", (ctx, arg) -> {
            Optional<Event> event = ctx.getScriptInstance().getEvent();
            if (!event.isPresent()) {
                return Result.<Boolean>builder().failure().error("There is no Sponge event for this trigger.").build();
            }

            if (!(event.get() instanceof Cancellable)) {
                return Result.<Boolean>builder().failure().error("This trigger cannot be cancelled.").build();
            }

            boolean cancel = ctx.getLiteral("Cancel", true).getBoolean();
            ((Cancellable) event.get()).setCancelled(cancel);
            return Result.<Boolean>builder().success().result(cancel).build(); // Return the cancel state
        }, GenericArguments.requiredArguments((Argument) null, (Argument) null, GenericArguments.withName("Cancel"))));

        register(this.createCompartment("setbasedamage", (ctx, arg) -> {
            Optional<Event> event = ctx.getScriptInstance().getEvent();
            if (!event.isPresent()) {
                return Result.builder().failure().error("There is no Sponge event for this trigger.").build();
            }

            if (!(event.get() instanceof DamageEntityEvent)) {
                return Result.builder().failure().error("This must be a damage event for this action.").build();
            }

            double damage = ctx.getLiteral("Damage").getNumber();
            ((DamageEntityEvent) event.get()).setBaseDamage(damage);
            return Result.success();
        }, GenericArguments.requiredArguments((Argument) null, GenericArguments.withName("Damage"))));

        register(this.createCompartment("setdamage", (ctx, arg) -> {
            Optional<Event> event = ctx.getScriptInstance().getEvent();
            if (!event.isPresent()) {
                return Result.builder().failure().error("There is no Sponge event for this trigger.").build();
            }

            if (!(event.get() instanceof DamageEntityEvent)) {
                return Result.builder().failure().error("This must be a damage event for this action.").build();
            }

            double damage = ctx.getLiteral("Damage").getNumber();
            Set<DamageModifierType> types = Sets.newHashSet(DamageModifierTypes.ABSORPTION, DamageModifierTypes.ARMOR,
                    DamageModifierTypes.ARMOR_ENCHANTMENT, DamageModifierTypes.MAGIC, DamageModifierTypes.DEFENSIVE_POTION_EFFECT, DamageModifierTypes.DIFFICULTY,
                    DamageModifierTypes.HARD_HAT, DamageModifierTypes.NEGATIVE_POTION_EFFECT, DamageModifierTypes.OFFENSIVE_POTION_EFFECT, DamageModifierTypes.WEAPON_ENCHANTMENT);
            DamageModifier modifier = DamageModifier.builder()
                    .type(DamageModifierTypes.BLOCKING)
                    .cause(Cause.of(NamedCause.source(this)))
                    .build();

            ((DamageEntityEvent) event.get()).addModifierAfter(modifier, (dmg) -> damage - dmg, types);
            return Result.success();
        }, GenericArguments.requiredArguments((Argument) null, GenericArguments.withName("Damage"))));

        // TODO fix damage now!!
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
