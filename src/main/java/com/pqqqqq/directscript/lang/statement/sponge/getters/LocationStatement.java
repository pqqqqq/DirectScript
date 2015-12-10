package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the {@link Location} of an entity
 */
public class LocationStatement extends Statement<Object> {

    public LocationStatement() {
        super(Syntax.builder()
                .identifiers("location", "loc")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Location> locationOptional = ctx.getLiteral("Object", Location.class).getAs(Location.class);
        if (!locationOptional.isPresent()) {
            return Result.builder().failure().result("Location object is not present").build();
        }

        // Getters
        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("array")) {
            Object[] result = new Object[]{((World) locationOptional.get().getExtent()).getName(), locationOptional.get().getX(), locationOptional.get().getY(), locationOptional.get().getZ()};
            return Result.builder().success().result(result).build();
        } else if (getter.equalsIgnoreCase("string")) {
            return Result.builder().success().result("{" + ((World) locationOptional.get().getExtent()).getName() + ", " + locationOptional.get().getX() + ", " + locationOptional.get().getY() + ", " + locationOptional.get().getZ() + "}").build();
        } else if (getter.equalsIgnoreCase("world")) {
            return Result.builder().success().result(locationOptional.get().getExtent()).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Literal.Literals.EMPTY_ARRAY).getArray();
        if (getter.equalsIgnoreCase("distance") || getter.equalsIgnoreCase("dist")) {
            Optional<Location> other = extraArguments.get(0).get().getAs(Location.class);
            boolean squared = extraArguments.size() > 1 ? extraArguments.get(1).get().getBoolean() : false;

            if (other.isPresent()) {
                Extent e1 = locationOptional.get().getExtent(), e2 = other.get().getExtent();
                Vector3d pos1 = locationOptional.get().getPosition(), pos2 = other.get().getPosition();
                double dist = (!e1.equals(e2) ? -1D : (squared ? pos1.distanceSquared(pos2) : pos1.distance(pos2)));

                return Result.builder().success().result(dist).build();
            } else {
                return Result.builder().failure().result("Distance counterpart not found").build();
            }
        }

        return Result.builder().failure().result("No suitable getter found").build();
    }
}
