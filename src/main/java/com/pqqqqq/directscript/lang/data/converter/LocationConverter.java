package com.pqqqqq.directscript.lang.data.converter;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-13.
 */
public class LocationConverter extends Converter<Location> {
    public static final Map<Class, Function<Location, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Vector3d.class, Location::getPosition);
        CONVERSION_MAP.put(BiomeType.class, Location::getBiome);
        CONVERSION_MAP.put(Vector3i.class, Location::getBlockPosition);
        CONVERSION_MAP.put(Vector2i.class, Location::getBiomePosition);
        CONVERSION_MAP.put(TileEntity.class, (location) -> location.getTileEntity().get());
    }

    private LocationConverter() {
        super(Location.class, CONVERSION_MAP, Converters.DATA_HOLDER_CONVERTER);
        this.addInheritance(Converters.BLOCK_STATE_CONVERTER, Location::getBlock);
        this.addInheritance(Converters.WORLD_CONVERTER, (location) -> (World) location.getExtent());
    }

    public static LocationConverter newInstance() {
        return new LocationConverter();
    }
}
