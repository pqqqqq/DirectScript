package com.pqqqqq.directscript.lang.data.converter;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class ItemStackConverter extends Converter<ItemStack> {
    public static final Map<Class, Function<ItemStack, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(ItemType.class, ItemStack::getItem);
        CONVERSION_MAP.put(ItemStackSnapshot.class, ItemStack::createSnapshot);
        CONVERSION_MAP.put(Double.class, ItemStack::getQuantity);
        CONVERSION_MAP.put(String.class, (item) -> item.getItem().getName());
        CONVERSION_MAP.put(BlockType.class, (item) -> item.getItem().getBlock().get());
    }

    private ItemStackConverter() {
        super(ItemStack.class, CONVERSION_MAP, Converters.DATA_HOLDER_CONVERTER);
    }

    public static ItemStackConverter newInstance() {
        return new ItemStackConverter();
    }
}
