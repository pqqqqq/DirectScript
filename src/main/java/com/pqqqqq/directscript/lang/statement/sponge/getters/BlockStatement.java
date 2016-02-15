package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataQuery;

import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_BLOCK;

/**
 * Created by Kevin on 2015-06-28.
 * A statement of getters for blocks
 */
public class BlockStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("block")
            .prefix("@")
            .build();

    public BlockStatement() {
        super();

        final Arguments[] GET_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<BlockSnapshot, String>createCompartment("id", (ctx, argument) -> {
            return Result.<String>builder().success().result(argument.getState().getType().getId()).build();
        }, GET_ARGUMENTS));

        register(this.<BlockSnapshot, String>createCompartment("name", (ctx, argument) -> {
            return Result.<String>builder().success().result(argument.getState().getType().getName()).build();
        }, GET_ARGUMENTS));

        register(this.<BlockSnapshot, Object>createCompartment("variant", (ctx, argument) -> {
            Optional<BlockTrait<?>> blockTrait = argument.getState().getTrait("variant");
            if (blockTrait.isPresent()) {
                Optional<?> variant = argument.getState().getTraitValue(blockTrait.get());
                if (variant.isPresent()) {
                    return Result.builder().success().result(variant.get().toString()).build();
                }
            }

            return Result.success();
        }, GET_ARGUMENTS));

        register(this.<BlockSnapshot, Object>createCompartment("set", (ctx, argument) -> {
            Optional<BlockState> blockState = ctx.getLiteral("BlockType", BlockState.class).getAs(BlockState.class);
            if (blockState.isPresent()) {
                argument.getLocation().get().setBlock(blockState.get());
                return Result.success();
            } else {
                Optional<BlockType> type = ctx.getLiteral("BlockType", BlockType.class).getAs(BlockType.class);
                if (!type.isPresent()) {
                    return Result.builder().failure().error("Unknown block type/state").build();
                }

                argument.getLocation().get().setBlockType(type.get());
                return Result.success();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("BlockType"))));

        register(this.<BlockSnapshot, Object>createCompartment("setdamage", (ctx, argument) -> {
            argument.toContainer().set(DataQuery.of("UnsafeDamage"), ctx.getLiteral("Damage").getNumber());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Damage"))));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_BLOCK;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
