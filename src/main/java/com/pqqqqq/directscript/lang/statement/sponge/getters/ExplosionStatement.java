package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.world.explosion.Explosion;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_EXPLOSION;

/**
 * Created by Kevin on 2015-11-14.
 * An explosion getter
 */
public class ExplosionStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("explosion")
            .prefix("@")
            .build();

    /**
     * Creates a new explosion statement
     */
    public ExplosionStatement() {
        super();

        final Arguments[] GET_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<Explosion, Vector3d>createCompartment("origin", (ctx, explosion) -> {
            return Result.<Vector3d>builder().success().result(explosion.getOrigin()).build();
        }, GET_ARGUMENTS));

        register(this.<Explosion, Float>createCompartment("radius", (ctx, explosion) -> {
            return Result.<Float>builder().success().result(explosion.getRadius()).build();
        }, GET_ARGUMENTS));

        register(this.<Explosion, Boolean>createCompartment("fire", (ctx, explosion) -> {
            return Result.<Boolean>builder().success().result(explosion.canCauseFire()).build();
        }, GET_ARGUMENTS));

        register(this.<Explosion, Boolean>createCompartment("breakblocks", (ctx, explosion) -> {
            return Result.<Boolean>builder().success().result(explosion.shouldBreakBlocks()).build();
        }, GET_ARGUMENTS));

        register(this.<Explosion, Boolean>createCompartment("smoke", (ctx, explosion) -> {
            return Result.<Boolean>builder().success().result(explosion.shouldPlaySmoke()).build();
        }, GET_ARGUMENTS));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_EXPLOSION;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
