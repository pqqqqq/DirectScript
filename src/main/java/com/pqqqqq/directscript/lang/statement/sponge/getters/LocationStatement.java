package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.world.Location;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the {@link Location} of a player
 */
public class LocationStatement extends Statement<Double[]> {

    public LocationStatement() {
        super(Syntax.builder()
                .identifiers("location", "loc")
                .prefix("@")
                .arguments(Arguments.empty(), Arguments.of(Argument.from("Location")))
                .build());
    }

    @Override
    public Result<Double[]> run(Context ctx) {
        Optional<Location> locationOptional = ctx.getLiteral("Location").getLocation();
        if (!locationOptional.isPresent()) {
            return Result.failure();
        }

        Double[] result = new Double[]{locationOptional.get().getX(), locationOptional.get().getY(), locationOptional.get().getZ()};
        return Result.<Double[]>builder().success().result(result).literal(result).build();
    }
}
