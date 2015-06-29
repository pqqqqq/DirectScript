package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.world.World;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for worlds
 */
public class WorldStatement extends Statement<Object> {

    public WorldStatement() {
        super(Syntax.builder()
                .identifiers("world")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Getter"), ",", Argument.from("World")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<World> worldOptional = ctx.getLiteral("World", World.class).getAs(World.class);
        if (!worldOptional.isPresent()) {
            return Result.failure();
        }

        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(worldOptional.get().getName()).literal(worldOptional.get().getName()).build();
        } else if (getter.equalsIgnoreCase("difficulty")) {
            return Result.builder().success().result(worldOptional.get().getDifficulty().getId()).literal(worldOptional.get().getDifficulty().getId()).build();
        }

        return Result.failure();
    }
}
