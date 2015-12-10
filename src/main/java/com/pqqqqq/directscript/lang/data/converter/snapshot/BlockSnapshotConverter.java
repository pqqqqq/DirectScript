package com.pqqqqq.directscript.lang.data.converter.snapshot;

import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.block.BlockSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class BlockSnapshotConverter extends Converter<BlockSnapshot> {
    public static final Map<Class, Function<BlockSnapshot, ?>> CONVERSION_MAP = new HashMap<>();

    private BlockSnapshotConverter() {
        super(BlockSnapshot.class, CONVERSION_MAP, Converters.LOCATEABLE_SNAPSHOT_CONVERTER);
        this.addInheritance(Converters.BLOCK_STATE_CONVERTER, BlockSnapshot::getState);
    }

    public static BlockSnapshotConverter newInstance() {
        return new BlockSnapshotConverter();
    }
}
