package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-11-14.
 * An explosion getter
 */
public class ExplosionStatement extends Statement<Object> {

    /**
     * Creates a new explosion statement
     */
    public ExplosionStatement() {
        super(Syntax.builder()
                .identifiers("explosion")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Explosion> explosion = ctx.getLiteral("Object", Explosion.class).getAs(Explosion.class);
        if (!explosion.isPresent()) {
            return Result.builder().failure().result("Explosion object is not present").build();
        }

        // Getters
        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("origin")) {
            return Result.builder().success().result(explosion.get().getOrigin()).build();
        } else if (getter.equalsIgnoreCase("radius")) {
            return Result.builder().success().result(explosion.get().getRadius()).build();
        } else if (getter.equalsIgnoreCase("fire")) {
            return Result.builder().success().result(explosion.get().canCauseFire()).build();
        } else if (getter.equalsIgnoreCase("breakblocks")) {
            return Result.builder().success().result(explosion.get().shouldBreakBlocks()).build();
        } else if (getter.equalsIgnoreCase("smoke")) {
            return Result.builder().success().result(explosion.get().shouldPlaySmoke()).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Literal.Literals.EMPTY_ARRAY).getArray();
        if (getter.equalsIgnoreCase("setradius")) {
            explosion.get().setRadius(extraArguments.get(0).get().getNumber().floatValue());
            return Result.success();
        } else if (getter.equalsIgnoreCase("setfire")) {
            explosion.get().canCauseFire(extraArguments.get(0).get().getBoolean());
            return Result.success();
        } else if (getter.equalsIgnoreCase("setsmoke")) {
            explosion.get().canPlaySmoke(extraArguments.get(0).get().getBoolean());
            return Result.success();
        } else if (getter.equalsIgnoreCase("setbreakblocks")) {
            explosion.get().shouldBreakBlocks(extraArguments.get(0).get().getBoolean());
            return Result.success();
        } else if (getter.equalsIgnoreCase("setorigin")) {
            Optional<Vector3d> origin = extraArguments.get(0).get().getAs(Vector3d.class);
            if (origin.isPresent()) {
                explosion.get().setOrigin(origin.get());
                return Result.success();
            } else {
                return Result.builder().failure().result("Origin vector not found").build();
            }
        }

        return Result.builder().failure().result("Unknown getter: " + getter).build();
    }
}
