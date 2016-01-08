package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_ITEM_STACK;

/**
 * Created by Kevin on 2015-06-28.
 * A statement of getters for item stacks
 */
public class ItemStackStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("item")
            .prefix("@")
            .build();

    public ItemStackStatement() {
        super();

        final Arguments[] GET_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<ItemStack, String>createCompartment("id", (ctx, itemStack) -> {
            return Result.<String>builder().success().result(itemStack.getItem().getId()).build();
        }, GET_ARGUMENTS));

        register(this.<ItemStack, String>createCompartment("name", (ctx, itemStack) -> {
            return Result.<String>builder().success().result(itemStack.getItem().getName()).build();
        }, GET_ARGUMENTS));

        register(this.<ItemStack, Integer>createCompartment("amount", (ctx, itemStack) -> {
            return Result.<Integer>builder().success().result(itemStack.getQuantity()).build();
        }, GET_ARGUMENTS));

        register(this.<ItemStack, Object>createCompartment("damage", (ctx, itemStack) -> {
            return Result.builder().success().result(itemStack.toContainer().get(DataQuery.of("UnsafeDamage")).get()).build();
        }, GET_ARGUMENTS));

        register(this.<ItemStack, Text>createCompartment("displayname", (ctx, itemStack) -> {
            return Result.<Text>builder().success().result(itemStack.get(Keys.DISPLAY_NAME).orElse(null)).build();
        }, GET_ARGUMENTS));

        register(this.<ItemStack, List<String>>createCompartment("lore", (ctx, itemStack) -> {
            List<Text> texts = itemStack.get(Keys.ITEM_LORE).orElse(null);
            List<String> strings = null;
            if (texts != null) {
                strings = new ArrayList<>();
                for (Text text : texts) {
                    strings.add(TextSerializers.PLAIN.serialize(text));
                }
            }

            return Result.<List<String>>builder().success().result(strings).build();
        }, GET_ARGUMENTS));

        register(this.<ItemStack, ItemStack>createCompartment("setdisplayname", (ctx, itemStack) -> {
            Optional<Text> text = ctx.getLiteral("DisplayName", Text.class).getAs(Text.class);
            if (text.isPresent()) {
                itemStack.offer(Keys.SHOWS_DISPLAY_NAME, true);
                itemStack.offer(Keys.DISPLAY_NAME, text.get());
                return Result.<ItemStack>builder().success().result(itemStack).build();
            } else {
                return Result.<ItemStack>builder().failure().error("Unknown text object").build();
            }
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("DisplayName"))));

        register(this.<ItemStack, ItemStack>createCompartment("setlore", (ctx, itemStack) -> {
            List<Text> texts = new ArrayList<>();
            List<Literal> array = ctx.getLiteral("Lore").getArray();

            array.forEach((literal -> texts.add(Utilities.getText(literal.getString()))));

            itemStack.offer(Keys.ITEM_LORE, texts);
            return Result.<ItemStack>builder().success().result(itemStack).build();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Lore"))));

        register(this.<ItemStack, Object>createCompartment("setdamage", (ctx, itemStack) -> {
            itemStack.toContainer().set(DataQuery.of("UnsafeDamage"), ctx.getLiteral("Damage").getNumber());
            return Result.success();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Damage"))));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_ITEM_STACK;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
