package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_WORLD;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for worlds
 */
public class WorldStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("world")
            .prefix("@")
            .build();

    public WorldStatement() {
        super();

        final Arguments[] GETTER_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<World, String>createCompartment("name", (ctx, world) -> {
            return Result.<String>builder().success().result(world.getName()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, String>createCompartment("difficulty", (ctx, world) -> {
            return Result.<String>builder().success().result(world.getDifficulty().getId()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Location>createCompartment("spawn", (ctx, world) -> {
            return Result.<Location>builder().success().result(world.getSpawnLocation()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Long>createCompartment("time", (ctx, world) -> {
            return Result.<Long>builder().success().result(world.getProperties().getWorldTime()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Boolean>createCompartment("pvp", (ctx, world) -> {
            return Result.<Boolean>builder().success().result(world.getProperties().isPVPEnabled()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Boolean>createCompartment(new String[]{"rain", "raining"}, (ctx, world) -> {
            return Result.<Boolean>builder().success().result(world.getProperties().isRaining()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Boolean>createCompartment(new String[]{"thunder", "thundering"}, (ctx, world) -> {
            return Result.<Boolean>builder().success().result(world.getProperties().isThundering()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Long>createCompartment("seed", (ctx, world) -> {
            return Result.<Long>builder().success().result(world.getProperties().getSeed()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Collection<Entity>>createCompartment("entities", (ctx, world) -> {
            return Result.<Collection<Entity>>builder().success().result(world.getEntities()).build();
        }, GETTER_ARGUMENTS));

        register(this.<World, Object>createCompartment("settime", (ctx, world) -> {
            world.getProperties().setWorldTime(ctx.getLiteral("Ticks").getNumber().intValue());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Ticks"))));

        register(this.<World, Object>createCompartment("setpvp", (ctx, world) -> {
            world.getProperties().setPVPEnabled(ctx.getLiteral("Enabled").getBoolean());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Enabled"))));

        register(this.<World, Object>createCompartment(new String[]{"setrain", "setraining"}, (ctx, world) -> {
            world.getProperties().setRaining(ctx.getLiteral("Enabled").getBoolean());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Enabled"))));

        register(this.<World, Object>createCompartment(new String[]{"setthunder", "setthundering"}, (ctx, world) -> {
            world.getProperties().setThundering(ctx.getLiteral("Enabled").getBoolean());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Enabled"))));

        register(this.<World, Object>createCompartment(new String[]{"playsound", "sound", "play"}, (ctx, world) -> {
            String sound = ctx.getLiteral("Sound").getString();
            Optional<SoundType> soundType = DirectScript.instance().getGame().getRegistry().getType(SoundType.class, sound);

            if (soundType.isPresent()) {
                Optional<Vector3d> position = ctx.getLiteral("Position", Vector3d.class).getAs(Vector3d.class);
                if (position.isPresent()) {
                    double volume = ctx.getLiteral("Volume", 1D).getNumber();
                    double pitch = ctx.getLiteral("Pitch", 0D).getNumber();
                    world.playSound(soundType.get(), position.get(), volume, pitch);
                    return Result.success();
                } else {
                    return Result.builder().failure().error("Position vector unavailable").build();
                }
            } else {
                return Result.builder().failure().error("Unknown sound type: " + sound).build();
            }
        }, GenericArguments.requiredArguments(this, new Argument[]{GenericArguments.withName("Sound"), GenericArguments.position("Position", 0, null)}, GenericArguments.withName("Volume"), GenericArguments.withName("Pitch"))));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_WORLD;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
