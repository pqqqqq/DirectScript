package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.entity.Entity;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for entities
 */
public class EntityStatement extends Statement<Object> {

    public EntityStatement() {
        super(Syntax.builder()
                .identifiers("entity")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Getter"), ",", Argument.from("Entity")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<Entity> entityOptional = ctx.getLiteral("Entity", Entity.class).getAs(Entity.class);
        if (!entityOptional.isPresent()) {
            return Result.failure();
        }

        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("location") || getter.equalsIgnoreCase("loc")) {
            return Result.builder().success().result(entityOptional.get().getLocation()).build();
        } else if (getter.equalsIgnoreCase("world")) {
            return Result.builder().success().result(entityOptional.get().getWorld()).build();
        } else if (getter.equalsIgnoreCase("type")) {
            return Result.builder().success().result(entityOptional.get().getType().getName()).build();
        } else if (getter.equalsIgnoreCase("uuid")) {
            return Result.builder().success().result(entityOptional.get().getUniqueId().toString()).build();
        }

        return Result.failure();
    }
}
