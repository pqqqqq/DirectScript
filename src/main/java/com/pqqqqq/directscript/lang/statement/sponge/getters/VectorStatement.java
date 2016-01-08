package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_VECTOR;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that gets the vector of an entity
 */
public class VectorStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("vector", "vec")
            .prefix("@")
            .build();

    public VectorStatement() {
        super();

        final Arguments[] GETTER_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<Vector3d, Double[]>createCompartment("array", (ctx, vector) -> {
            Double[] result = new Double[]{vector.getX(), vector.getY(), vector.getZ()};
            return Result.<Double[]>builder().success().result(result).build();
        }, GETTER_ARGUMENTS));

        register(this.<Vector3d, String>createCompartment("string", (ctx, vector) -> {
            return Result.<String>builder().success().result("{" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + "}").build();
        }, GETTER_ARGUMENTS));

        register(this.<Vector3d, Double>createCompartment("x", (ctx, vector) -> {
            return Result.<Double>builder().success().result(vector.getX()).build();
        }, GETTER_ARGUMENTS));

        register(this.<Vector3d, Double>createCompartment("y", (ctx, vector) -> {
            return Result.<Double>builder().success().result(vector.getY()).build();
        }, GETTER_ARGUMENTS));

        register(this.<Vector3d, Double>createCompartment("z", (ctx, vector) -> {
            return Result.<Double>builder().success().result(vector.getZ()).build();
        }, GETTER_ARGUMENTS));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_VECTOR;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
