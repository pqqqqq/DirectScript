package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_ENTITY;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for entities
 */
public class EntityStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("entity")
            .prefix("@")
            .build();

    public EntityStatement() {
        super();
        //this.<Entity, Location>inherit(Statements.LOCATION, Entity::getLocation); // Inherit location

        final Arguments[] GET_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<Entity, Location>createCompartment(new String[]{"location", "loc"}, (ctx, entity) -> {
            return Result.<Location>builder().success().result(entity.getLocation()).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, World>createCompartment("world", (ctx, entity) -> {
            return Result.<World>builder().success().result(entity.getWorld()).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, EntityType>createCompartment("type", (ctx, entity) -> {
            return Result.<EntityType>builder().success().result(entity.getType()).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, UUID>createCompartment("uuid", (ctx, entity) -> {
            return Result.<UUID>builder().success().result(entity.getUniqueId()).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, Object>createCompartment(new String[]{"kill", "remove"}, (ctx, entity) -> {
            entity.remove();
            return Result.success();
        }, GET_ARGUMENTS));

        register(this.<Entity, Vector3d>createCompartment("rotation", (ctx, entity) -> {
            return Result.<Vector3d>builder().success().result(entity.getRotation()).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, Vector3d>createCompartment("direction", (ctx, entity) -> {
            return Result.<Vector3d>builder().success().result(Utilities.getDirection(entity.getRotation())).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, Transform>createCompartment("transform", (ctx, entity) -> {
            return Result.<Transform>builder().success().result(entity.getTransform()).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, Vector3d>createCompartment("velocity", (ctx, entity) -> {
            return Result.<Vector3d>builder().success().result(entity.get(Keys.VELOCITY).get()).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, UUID>createCompartment("owner", (ctx, entity) -> {
            return Result.<UUID>builder().success().result(entity.get(Keys.TAMED_OWNER).get().orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<Entity, Object>createCompartment(new String[]{"teleport", "tp", "setlocation", "setloc"}, (ctx, entity) -> {
            Optional<Location> location = ctx.getLiteral("Location", Location.class).getAs(Location.class);
            boolean safe = ctx.getLiteral("Safe", false).getBoolean();

            if (location.isPresent()) {
                if (safe) {
                    entity.setLocationSafely(location.get());
                } else {
                    entity.setLocation(location.get());
                }
                return Result.success();
            } else {
                return Result.builder().failure().error("Teleportation location unavailable").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.location("Location", 0, null), GenericArguments.withName("Safe"))));

        register(this.<Entity, Object>createCompartment("setrotation", (ctx, entity) -> {
            Optional<Vector3d> rotation = ctx.getLiteral("Rotation", Vector3d.class).getAs(Vector3d.class);
            if (rotation.isPresent()) {
                entity.setRotation(rotation.get());
                return Result.success();
            } else {
                return Result.builder().failure().error("Rotation vector unavailable").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.rotation("Rotation", 0, null))));

        register(this.<Entity, Object>createCompartment("settransform", (ctx, entity) -> {
            Optional<Transform> transform = ctx.getLiteral("Transform", Transform.class).getAs(Transform.class);
            if (transform.isPresent()) {
                entity.setTransform(transform.get());
                return Result.success();
            } else {
                return Result.builder().failure().error("Transform unavailable").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.transform("Transform", 0, null))));

        register(this.<Entity, Object>createCompartment("setvelocity", (ctx, entity) -> {
            Optional<Vector3d> velocity = ctx.getLiteral("Velocity", Vector3d.class).getAs(Vector3d.class);
            if (velocity.isPresent()) {
                entity.offer(Keys.VELOCITY, velocity.get());
                return Result.success();
            } else {
                return Result.builder().failure().error("Velocity vector unavailable").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.velocity("Velocity", 0, null))));

        register(this.<Entity, Collection<Entity>>createCompartment(new String[]{"near", "nearby", "entitiesnearby", "closeentities"}, (ctx, entity) -> {
            final Vector3d pos = entity.getLocation().getPosition();
            double distance = ctx.getLiteral("Distance").getNumber();
            final boolean includeSelf = ctx.getLiteral("IncludeSelf", false).getBoolean();

            return Result.<Collection<Entity>>builder().success().result(entity.getLocation().getExtent().getEntities((checkEntity) -> {
                return checkEntity.getLocation().getPosition().distance(pos) <= distance && (includeSelf || !checkEntity.equals(entity));
            })).build();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Distance"), GenericArguments.withName("IncludeSelf"))));

        register(this.<Entity, Object>createCompartment("setowner", (ctx, entity) -> {
            Optional<UUID> owner = ctx.getLiteral("Owner", UUID.class).getAs(UUID.class);
            if (owner.isPresent()) {
                if (entity.supports(Keys.TAMED_OWNER)) {
                    entity.offer(Keys.TAMED_OWNER, owner);
                    return Result.success();
                } else {
                    return Result.builder().failure().error("This entity cannot be tamed").build();
                }
            } else {
                return Result.builder().failure().error("Owner unavailable").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Owner"))));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_ENTITY;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
