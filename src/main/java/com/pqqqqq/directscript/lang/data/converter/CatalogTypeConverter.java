package com.pqqqqq.directscript.lang.data.converter;

import org.spongepowered.api.CatalogType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-15.
 */
public class CatalogTypeConverter extends Converter<CatalogType> {
    public static final Map<Class, Function<CatalogType, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(String.class, CatalogType::getName); // TODO getId better?
    }

    private CatalogTypeConverter() {
        super(CatalogType.class, CONVERSION_MAP);
    }

    public static CatalogTypeConverter newInstance() {
        return new CatalogTypeConverter();
    }
}
