package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.collect.Lists;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-29.
 * A statement of getters for worlds
 */
public class WorldStatement extends Statement<Object> {

    public WorldStatement() {
        super(Syntax.builder()
                .identifiers("world")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<World> worldOptional = ctx.getLiteral("Object", World.class).getAs(World.class);
        if (!worldOptional.isPresent()) {
            return Result.builder().failure().result("World object not found").build();
        }

        // Getters
        String getter = ctx.getLiteral("Getter").getString();
        if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(worldOptional.get().getName()).build();
        } else if (getter.equalsIgnoreCase("difficulty")) {
            return Result.builder().success().result(worldOptional.get().getDifficulty().getId()).build();
        } else if (getter.equalsIgnoreCase("spawn")) {
            return Result.builder().success().result(worldOptional.get().getSpawnLocation()).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Lists::newArrayList).getArray();
        if (getter.equalsIgnoreCase("entities")) {
            return Result.builder().success().result(worldOptional.get().getEntities()).build();
        } else if (getter.equalsIgnoreCase("spawn") || getter.equalsIgnoreCase("summon")) {

        }

        return Result.builder().failure().result("Unknown getter: " + getter).build();
    }
}
