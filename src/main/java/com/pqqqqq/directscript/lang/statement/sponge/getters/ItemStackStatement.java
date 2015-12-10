package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.*;

/**
 * Created by Kevin on 2015-06-28.
 * A statement of getters for item stacks
 */
public class ItemStackStatement extends Statement<Object> {

    public ItemStackStatement() {
        super(Syntax.builder()
                .identifiers("item")
                .prefix("@")
                .arguments(Arguments.of(GETTER), Arguments.of(OBJECT, ",", GETTER), Arguments.of(GETTER, ",", ARGUMENTS))
                .arguments(Arguments.of(OBJECT, ",", GETTER, ",", ARGUMENTS))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        Optional<ItemStack> itemStack = ctx.getLiteral("Object", ItemStack.class).getAs(ItemStack.class);
        if (!itemStack.isPresent()) {
            return Result.builder().failure().result("ItemStack object is not present").build();
        }

        ItemType itemType = itemStack.get().getItem();
        String getter = ctx.getLiteral("Getter").getString();

        // Getters
        if (getter.equalsIgnoreCase("id")) {
            return Result.builder().success().result(itemType.getId()).build();
        } else if (getter.equalsIgnoreCase("name")) {
            return Result.builder().success().result(itemType.getName()).build();
        } else if (getter.equalsIgnoreCase("amount")) {
            return Result.builder().success().result(itemStack.get().getQuantity()).build();
        } else if (getter.equalsIgnoreCase("damage")) {
            return Result.builder().success().result(itemStack.get().toContainer().get(new DataQuery("UnsafeDamage")).get()).build();
        }

        // Extra args
        List<Datum> extraArguments = ctx.getLiteral("Arguments", Literal.Literals.EMPTY_ARRAY).getArray();
        if (getter.equalsIgnoreCase("displayname")) {
            return Result.builder().success().result(itemStack.get().get(Keys.DISPLAY_NAME).orElse(null)).build();
        } else if (getter.equalsIgnoreCase("lore")) {
            List<Text> texts = itemStack.get().get(Keys.ITEM_LORE).orElse(null);
            List<String> strings = null;
            if (texts != null) {
                strings = new ArrayList<>();
                for (Text text : texts) {
                    strings.add(Texts.toPlain(text));
                }
            }

            return Result.builder().success().result(strings).build();
        } else if (getter.equalsIgnoreCase("setdisplayname")) {
            Optional<Text> text = extraArguments.get(0).get().getAs(Text.class);
            if (text.isPresent()) {
                itemStack.get().offer(Keys.SHOWS_DISPLAY_NAME, true);
                itemStack.get().offer(Keys.DISPLAY_NAME, text.get());
                return Result.success();
            } else {
                return Result.builder().failure().result("Unknown text object").build();
            }
        } else if (getter.equalsIgnoreCase("setlore")) {
            List<Text> texts = new ArrayList<>();
            extraArguments.forEach((dataHolder -> texts.add(Texts.of(dataHolder.get().getString()))));

            itemStack.get().offer(Keys.ITEM_LORE, texts);
            return Result.success();
        }

        return Result.builder().failure().result("Unknown getter: " + getter).build();
    }
}
