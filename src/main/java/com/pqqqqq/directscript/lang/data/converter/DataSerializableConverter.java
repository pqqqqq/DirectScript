package com.pqqqqq.directscript.lang.data.converter;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class DataSerializableConverter extends Converter<DataSerializable> {
    public static final Map<Class, Function<DataSerializable, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(DataContainer.class, DataSerializable::toContainer);
    }

    private DataSerializableConverter() {
        super(DataSerializable.class, CONVERSION_MAP);
    }

    public static DataSerializableConverter newInstance() {
        return new DataSerializableConverter();
    }
}
