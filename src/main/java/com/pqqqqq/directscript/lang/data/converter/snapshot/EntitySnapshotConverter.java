package com.pqqqqq.directscript.lang.data.converter.snapshot;

import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.entity.EntitySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class EntitySnapshotConverter extends Converter<EntitySnapshot> {
    public static final Map<Class, Function<EntitySnapshot, ?>> CONVERSION_MAP = new HashMap<>();

    private EntitySnapshotConverter() {
        super(EntitySnapshot.class, CONVERSION_MAP, Converters.LOCATEABLE_SNAPSHOT_CONVERTER);
        this.addInheritance(Converters.ENTITY_CONVERTER, (snapshot) -> snapshot.restore().get());
    }

    public static EntitySnapshotConverter newInstance() {
        return new EntitySnapshotConverter();
    }
}
