package com.pqqqqq.directscript.lang.statement.sponge.setters;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.world.World;

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
                .arguments(Arguments.of(Argument.from("Location"), ",", Argument.from("BlockType")))
                .arguments(Arguments.of(Argument.from("World"), ",", Argument.from("Location"), ",", Argument.from("BlockType")))
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

        String blockType = ctx.getLiteral("BlockType", "minecraft:air").getString();
        Optional<BlockType> blockTypeOptional = Utilities.getType(BlockType.class, blockType.trim());
        if (!blockTypeOptional.isPresent()) {
            return Result.failure();
        }

        world.get().setBlockType(coordinates.get().toInt(), blockTypeOptional.get());
        return Result.success();
    }
}
