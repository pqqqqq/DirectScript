package com.pqqqqq.directscript.lang.data.converter;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class DataContainerConverter extends Converter<DataContainer> {
    public static final Map<Class, Function<DataContainer, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(DataQuery.class, DataContainer::getCurrentPath);
    }

    private DataContainerConverter() {
        super(DataContainer.class, CONVERSION_MAP);
    }

    public static DataContainerConverter newInstance() {
        return new DataContainerConverter();
    }
}
