package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;

import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-28.
 * A statement of getters for blocks
 */
public class BlockStatement extends Statement<Object> {

    public BlockStatement() {
        super(Syntax.builder()
                .identifiers("block")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<BlockSnapshot> block = ctx.getLiteral("Object", BlockSnapshot.class).getAs(BlockSnapshot.class);
        if (!block.isPresent()) {
            return Result.builder().failure().result("Block object not present").build();
        }

        BlockType blockType = block.get().getState().getType();
        String getter = ctx.getLiteral("Getter").getString();

        // Getters
        if (getter.equalsIgnoreCase("id")) {
            return Result.builder().success().result(blockType.getId()).build();
        } else if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(blockType.getName()).build();
        } else if (getter.equalsIgnoreCase("damage")) {
            return Result.builder().success().result(block.get().toContainer().get(new DataQuery("UnsafeDamage")).get()).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Literal.Literals.EMPTY_ARRAY).getArray();
        if (getter.equalsIgnoreCase("set")) {
            Literal arg = extraArguments.get(0).get();
            boolean notify = extraArguments.size() > 1 ? extraArguments.get(1).get().getBoolean() : false;

            Optional<BlockState> blockState = arg.getAs(BlockState.class);
            if (blockState.isPresent()) {
                block.get().getLocation().get().setBlock(blockState.get()/*, notify*/);
                return Result.success();
            } else {
                Optional<BlockType> type = arg.getAs(BlockType.class);
                if (!type.isPresent()) {
                    return Result.builder().failure().result("Unknown block type/state").build();
                }

                block.get().getLocation().get().setBlockType(type.get()/*, notify*/);
                return Result.success();
            }
        }

        return Result.builder().failure().result("Unknown getter: " + getter).build();
    }
}
