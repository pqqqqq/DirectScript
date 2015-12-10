package com.pqqqqq.directscript.lang.data.converter;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class BlockStateConverter extends Converter<BlockState> {
    public static final Map<Class, Function<BlockState, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(BlockType.class, BlockState::getType);
        CONVERSION_MAP.put(ItemType.class, (state) -> state.getType().getItem().get());
        CONVERSION_MAP.put(String.class, (state) -> state.getType().getName());
        CONVERSION_MAP.put(Translation.class, (state) -> state.getType().getTranslation());
    }

    private BlockStateConverter() {
        super(BlockState.class, CONVERSION_MAP, Converters.DATA_SERIALIZABLE_CONVERTER);
    }

    public static BlockStateConverter newInstance() {
        return new BlockStateConverter();
    }
}
