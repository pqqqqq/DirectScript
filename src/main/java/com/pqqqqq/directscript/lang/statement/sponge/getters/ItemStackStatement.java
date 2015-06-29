package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by Kevin on 2015-06-28.
 * A statement of getters for item stacks
 */
public class ItemStackStatement extends Statement<Object> {

    public ItemStackStatement() {
        super(Syntax.builder()
                .identifiers("item")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Getter"), ",", Argument.from("ItemStack")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<ItemStack> itemStack = ctx.getLiteral("ItemStack", ItemStack.class).getAs(ItemStack.class);
        if (!itemStack.isPresent()) {
            return Result.failure();
        }

        ItemType itemType = itemStack.get().getItem();
        String getter = ctx.getLiteral("Getter").getString();

        if (getter.equalsIgnoreCase("id")) {
            return Result.builder().success().result(itemType.getId()).literal(itemType.getId()).build();
        } else if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(itemType.getName()).literal(itemType.getName()).build();
        } else if (getter.equalsIgnoreCase("amount")) {
            return Result.builder().success().result(itemStack.get().getQuantity()).literal(itemStack.get().getQuantity()).build();
        }

        return Result.failure();
    }
}
