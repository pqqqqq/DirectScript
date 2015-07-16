package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the {@link Location} of an entity
 */
public class LocationStatement extends Statement<Object> {

    public LocationStatement() {
        super(Syntax.builder()
                .identifiers("location", "loc")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Getter"), ",", Argument.from("Location")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Location> locationOptional = ctx.getLiteral("Location", Location.class).getAs(Location.class);
        if (!locationOptional.isPresent()) {
            return Result.failure();
        }

        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("array")) {
            Object[] result = new Object[]{((World) locationOptional.get().getExtent()).getName(), locationOptional.get().getX(), locationOptional.get().getY(), locationOptional.get().getZ()};
            return Result.builder().success().result(result).build();
        } else if (getter.equalsIgnoreCase("string")) {
            return Result.builder().success().result("{" + ((World) locationOptional.get().getExtent()).getName() + ", " + locationOptional.get().getX() + ", " + locationOptional.get().getY() + ", " + locationOptional.get().getZ() + "}").build();
        } else if (getter.equalsIgnoreCase("world")) {
            return Result.builder().success().result(locationOptional.get().getExtent()).build();
        }

        return Result.failure();
    }
}
