package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

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
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Location> locationOptional = ctx.getLiteral("Location", Location.class).getAs(Location.class);
        if (!locationOptional.isPresent()) {
            return Result.failure();
        }

        Extent extent = locationOptional.get().getExtent();

        String entityType = ctx.getLiteral("EntityType").getString();
        Optional<EntityType> entityTypeOptional = Utilities.getType(EntityType.class, entityType);
        if (!entityTypeOptional.isPresent()) {
            return Result.failure();
        }

        int amount = ctx.getLiteral("Amount", 1).getNumber().intValue();
        for (int i = 0; i < amount; i++) {
            Entity newEntity = extent.createEntity(entityTypeOptional.get(), locationOptional.get().getBlockPosition()).get();
            extent.spawnEntity(newEntity);
        }

        return Result.success();
    }
}
