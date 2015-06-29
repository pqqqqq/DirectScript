package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;

/**
 * Created by Kevin on 2015-06-28.
 * A statement of getters for blocks
 */
public class BlockStatement extends Statement<Object> {

    public BlockStatement() {
        super(Syntax.builder()
                .identifiers("block")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Getter"), ",", Argument.from("Block")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<BlockSnapshot> block = ctx.getLiteral("Block").getBlock();
        if (!block.isPresent()) {
            return Result.failure();
        }

        BlockType blockType = block.get().getState().getType();
        String getter = ctx.getLiteral("Getter").getString();

        if (getter.equalsIgnoreCase("id")) {
            return Result.builder().success().result(blockType.getId()).literal(blockType.getId()).build();
        } else if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(blockType.getName()).literal(blockType.getName()).build();
        } else if (getter.equalsIgnoreCase("light")) {
            return Result.builder().success().result((double) blockType.getEmittedLight()).literal((double) blockType.getEmittedLight()).build();
        }

        return Result.failure();
    }
}
