package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.world.Location;

/**
 * Created by Kevin on 2015-06-24.
 * A statement that sets the material type and such at a location
 */
public class SetBlockStatement extends Statement {

    public SetBlockStatement() {
        super(Syntax.builder()
                .identifiers("setblock")
                .prefix("@")
                .arguments(Arguments.empty())
                .arguments(Arguments.of(Argument.from("BlockType")))
                .arguments(Arguments.of(Argument.from("Location"), ",", Argument.from("BlockType"))) // TODO: Data stuff?
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Optional<Location> locationOptional = ctx.getLiteral("Location", Location.class).getAs(Location.class);
        if (!locationOptional.isPresent()) {
            return Result.failure();
        }

        String blockType = ctx.getLiteral("BlockType", "minecraft:air").getString();
        Optional<BlockType> blockTypeOptional = Utilities.getType(BlockType.class, blockType.trim());
        if (!blockTypeOptional.isPresent()) {
            return Result.failure();
        }

        locationOptional.get().setBlockType(blockTypeOptional.get());
        return Result.success();
    }
}
