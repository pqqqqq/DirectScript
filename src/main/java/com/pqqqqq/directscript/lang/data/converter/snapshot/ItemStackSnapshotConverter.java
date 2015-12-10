package com.pqqqqq.directscript.lang.data.converter.snapshot;

import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class ItemStackSnapshotConverter extends Converter<ItemStackSnapshot> {
    public static final Map<Class, Function<ItemStackSnapshot, ?>> CONVERSION_MAP = new HashMap<>();

    private ItemStackSnapshotConverter() {
        super(ItemStackSnapshot.class, CONVERSION_MAP);
        this.addInheritance(Converters.ITEM_STACK_CONVERTER, ItemStackSnapshot::createStack);
    }

    public static ItemStackSnapshotConverter newInstance() {
        return new ItemStackSnapshotConverter();
    }
}
