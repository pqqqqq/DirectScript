package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the vector of an entity
 */
public class VectorStatement extends Statement<Object> {

    public VectorStatement() {
        super(Syntax.builder()
                .identifiers("vector", "vec")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Vector3d> vectorOptional = ctx.getLiteral("Object", Vector3d.class).getAs(Vector3d.class);
        if (!vectorOptional.isPresent()) {
            return Result.builder().failure().result("Vector object not found").build();
        }

        // Getter
        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("array")) {
            Double[] result = new Double[]{vectorOptional.get().getX(), vectorOptional.get().getY(), vectorOptional.get().getZ()};
            return Result.builder().success().result(result).build();
        } else if (getter.equalsIgnoreCase("string")) {
            return Result.builder().success().result("{" + vectorOptional.get().getX() + ", " + vectorOptional.get().getY() + ", " + vectorOptional.get().getZ() + "}").build();
        } else if (getter.equalsIgnoreCase("x")) {
            return Result.builder().success().result(vectorOptional.get().getX()).build();
        } else if (getter.equalsIgnoreCase("y")) {
            return Result.builder().success().result(vectorOptional.get().getY()).build();
        } else if (getter.equalsIgnoreCase("z")) {
            return Result.builder().success().result(vectorOptional.get().getZ()).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Lists::newArrayList).getArray();
        // TODO: shitload of vector stuff

        return Result.builder().failure().result("Unknown getter: " + getter).build();
    }
}
