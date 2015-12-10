package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Location;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for entities
 */
public class EntityStatement extends Statement<Object> {

    public EntityStatement() {
        super(Syntax.builder()
                .identifiers("entity")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Entity> entityOptional = ctx.getLiteral("Object", Entity.class).getAs(Entity.class);
        if (!entityOptional.isPresent()) {
            return Result.builder().failure().result("Entity object not present").build();
        }

        // Getters
        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("location") || getter.equalsIgnoreCase("loc")) {
            return Result.builder().success().result(entityOptional.get().getLocation()).build();
        } else if (getter.equalsIgnoreCase("world")) {
            return Result.builder().success().result(entityOptional.get().getWorld()).build();
        } else if (getter.equalsIgnoreCase("type")) {
            return Result.builder().success().result(entityOptional.get().getType()).build();
        } else if (getter.equalsIgnoreCase("uuid")) {
            return Result.builder().success().result(entityOptional.get().getUniqueId()).build();
        } else if (getter.equalsIgnoreCase("kill") || getter.equalsIgnoreCase("remove")) {
            entityOptional.get().remove();
            return Result.success();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Literal.Literals.EMPTY_ARRAY).getArray();
        if (getter.equalsIgnoreCase("teleport") || getter.equalsIgnoreCase("tp") || getter.equalsIgnoreCase("setloc") || getter.equalsIgnoreCase("setlocation")) {
            Optional<Location> location = extraArguments.get(0).get().getAs(Location.class);
            boolean safe = extraArguments.size() > 1 ? extraArguments.get(1).get().getBoolean() : false;

            if (location.isPresent()) {
                if (safe) {
                    entityOptional.get().setLocationSafely(location.get());
                } else {
                    entityOptional.get().setLocation(location.get());
                }
                return Result.success();
            } else {
                return Result.builder().failure().result("Teleportation location unavailable").build();
            }
        } else if (getter.equalsIgnoreCase("setrotation")) {
            Optional<Vector3d> rotation = extraArguments.get(0).get().getAs(Vector3d.class);
            if (rotation.isPresent()) {
                entityOptional.get().setRotation(rotation.get());
                return Result.success();
            }
        } else if (getter.equalsIgnoreCase("setscale")) {
            Optional<Vector3d> rotation = extraArguments.get(0).get().getAs(Vector3d.class);
            if (rotation.isPresent()) {
                entityOptional.get().setScale(rotation.get());
                return Result.success();
            } else {
                return Result.builder().failure().result("Rotation vector unavailable").build();
            }
        } else if (getter.equalsIgnoreCase("settransform")) {
            Optional<Transform> transform = extraArguments.get(0).get().getAs(Transform.class);
            if (transform.isPresent()) {
                entityOptional.get().setTransform(transform.get());
                return Result.success();
            } else {
                return Result.builder().failure().result("Transform unavailable").build();
            }
        } else if (getter.equalsIgnoreCase("nearby") || getter.equalsIgnoreCase("entitiesnearby") || getter.equalsIgnoreCase("closeentities")) {
            final Vector3d pos = entityOptional.get().getLocation().getPosition();
            final boolean includeSelf = extraArguments.size() > 1 ? extraArguments.get(1).get().getBoolean() : false;

            return Result.builder().success().result(entityOptional.get().getLocation().getExtent().getEntities((entity) -> {
                return entity.getLocation().getPosition().distance(pos) <= extraArguments.get(0).get().getNumber() && (includeSelf || !entity.equals(entityOptional.get()));
            })).build();
        }

        return Result.builder().failure().result("Unknown getter " + getter).build();
    }
}
