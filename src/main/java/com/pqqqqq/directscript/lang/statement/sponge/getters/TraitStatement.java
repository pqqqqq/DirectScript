package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;

import java.util.Optional;

/**
 * Created by Kevin on 2015-11-16.
 * A statement that returns the block trait value for a block
 */
public class TraitStatement extends Statement<Object> {

    public TraitStatement() {
        super(Syntax.builder()
                .identifiers("trait", "blocktrait")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Trait")), Arguments.of(Argument.from("Trait"), ",", Argument.from("BlockState")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<BlockState> blockState = ctx.getLiteral("BlockState", BlockState.class).getAs(BlockState.class);
        if (!blockState.isPresent()) {
            return Result.failure();
        }

        Optional<BlockTrait<?>> trait = blockState.get().getTrait(ctx.getLiteral("Trait").getString());
        if (!trait.isPresent()) {
            return Result.failure();
        }

        return Result.builder().success().result(blockState.get().getTraitValue(trait.get()).orElse(null)).build();
    }
}
