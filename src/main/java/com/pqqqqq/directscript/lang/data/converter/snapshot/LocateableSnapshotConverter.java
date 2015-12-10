package com.pqqqqq.directscript.lang.data.converter.snapshot;

import com.flowpowered.math.vector.Vector3d;
import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.data.LocateableSnapshot;
import org.spongepowered.api.world.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class LocateableSnapshotConverter extends Converter<LocateableSnapshot> {
    public static final Map<Class, Function<LocateableSnapshot, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Vector3d.class, (snapshot) -> snapshot.getPosition().toDouble());
    }

    private LocateableSnapshotConverter() {
        super(LocateableSnapshot.class, CONVERSION_MAP, Converters.DATA_SERIALIZABLE_CONVERTER);
        this.addInheritance(Converters.LOCATION_CONVERTER, (snapshot) -> (Location) snapshot.getLocation().get());
    }

    public static LocateableSnapshotConverter newInstance() {
        return new LocateableSnapshotConverter();
    }
}
