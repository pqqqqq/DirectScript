package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-07-11.
 * Returns the distance between two vectors
 */
public class DistanceStatement extends Statement<Double> {

    public DistanceStatement() {
        super(Syntax.builder()
                .identifiers("distance")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Vector1"), ",", Argument.from("Vector2")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        Optional<Vector3d> vector1Optional = ctx.getLiteral("Vector1", Vector3d.class).getAs(Vector3d.class);
        if (!vector1Optional.isPresent()) {
            return Result.failure();
        }

        Optional<Vector3d> vector2Optional = ctx.getLiteral("Vector2", Vector3d.class).getAs(Vector3d.class);
        if (!vector2Optional.isPresent()) {
            return Result.failure();
        }

        double dist = vector1Optional.get().distance(vector2Optional.get());
        return Result.<Double>builder().success().result(dist).build();
    }
}
