package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.World;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that spawns a new entity
 */
public class SpawnStatement extends Statement {

    public SpawnStatement() {
        super(Syntax.builder()
                .identifiers("spawn")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("EntityType")))
                .arguments(Arguments.of(Argument.from("EntityType"), ",", Argument.from("Amount")))
                .arguments(Arguments.of(Argument.from("Location"), ",", Argument.from("EntityType"), ",", Argument.from("Amount")))
                .arguments(Arguments.of(Argument.from("World"), ",", Argument.from("Location"), ",", Argument.from("EntityType"), ",", Argument.from("Amount")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<World> world = ctx.getLiteral("World").getWorld();
        if (!world.isPresent()) {
            return Result.failure();
        }

        Optional<Vector3d> coordinates = ctx.getLiteral("Location").getVector();
        if (!coordinates.isPresent()) {
            return Result.failure();
        }

        String entityType = ctx.getLiteral("EntityType").getString();
        Optional<EntityType> entityTypeOptional = Utilities.getType(EntityType.class, entityType);
        if (!entityTypeOptional.isPresent()) {
            return Result.failure();
        }

        int amount = ctx.getLiteral("Amount", 1).getNumber().intValue();
        for (int i = 0; i < amount; i++) {
            Entity newEntity = world.get().createEntity(entityTypeOptional.get(), coordinates.get().toInt()).get();
            world.get().spawnEntity(newEntity);
        }

        return Result.success();
    }
}
