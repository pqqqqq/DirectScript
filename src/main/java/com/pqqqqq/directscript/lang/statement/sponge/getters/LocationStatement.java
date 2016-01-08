package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_LOCATION;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the {@link Location} of an entity
 */
public class LocationStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("location", "loc")
            .prefix("@")
            .build();

    public LocationStatement() {
        super();

        final Arguments[] GET_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<Location, Object[]>createCompartment("array", (ctx, location) -> {
            Object[] result = new Object[]{((World) location.getExtent()).getName(), location.getX(), location.getY(), location.getZ()};
            return Result.<Object[]>builder().success().result(result).build();
        }, GET_ARGUMENTS));

        register(this.<Location, String>createCompartment("string", (ctx, location) -> {
            return Result.<String>builder().success().result("{" + ((World) location.getExtent()).getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + "}").build();
        }, GET_ARGUMENTS));

        register(this.<Location, World>createCompartment("world", (ctx, location) -> {
            return Result.<World>builder().success().result((World) location.getExtent()).build();
        }, GET_ARGUMENTS));

        register(this.<Location, Double>createCompartment(new String[]{"distance", "dist"}, (ctx, location) -> {
            Optional<Location> other = ctx.getLiteral("Other", Location.class).getAs(Location.class);
            boolean squared = ctx.getLiteral("Squared", false).getBoolean();

            if (other.isPresent()) {
                Extent e1 = location.getExtent(), e2 = other.get().getExtent();
                Vector3d pos1 = location.getPosition(), pos2 = other.get().getPosition();
                double dist = (!e1.equals(e2) ? -1D : (squared ? pos1.distanceSquared(pos2) : pos1.distance(pos2)));

                return Result.<Double>builder().success().result(dist).build();
            } else {
                return Result.<Double>builder().failure().error("Distance counterpart not found").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.location("Other", 0, null), GenericArguments.withName("Squared"))));

        register(this.<Location, Object>createCompartment("spawn", (ctx, location) -> {
            Extent extent = location.getExtent();

            Optional<EntityType> entityTypeOptional = ctx.getLiteral("EntityType", EntityType.class).getAs(EntityType.class);
            if (!entityTypeOptional.isPresent()) {
                return Result.builder().failure().error("Unknown entity type").build();
            }

            int amount = ctx.getLiteral("Amount", 1).getNumber().intValue();

            List<Entity> entityList = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                Entity newEntity = extent.createEntity(entityTypeOptional.get(), location.getBlockPosition()).get();
                extent.spawnEntity(newEntity, Cause.of(DirectScript.instance())); // DirectScript is our cause?

                entityList.add(newEntity);
            }

            Object result = (entityList.isEmpty() ? null : entityList.size() == 1 ? entityList.get(0) : entityList);
            return Result.builder().success().result(result).build();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("EntityType"), GenericArguments.withName("Amount"))));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_LOCATION;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
