package com.pqqqqq.directscript.lang.data.converter.vector;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector3l;
import com.pqqqqq.directscript.lang.data.converter.Converter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-12-28.
 */
public class Vector3fConverter extends Converter<Vector3f> {
    public static final Map<Class, Function<Vector3f, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Vector3i.class, Vector3f::toInt);
        CONVERSION_MAP.put(Vector3d.class, Vector3f::toDouble);
        CONVERSION_MAP.put(Vector3l.class, Vector3f::toLong);
    }

    private Vector3fConverter() {
        super(Vector3f.class, CONVERSION_MAP);
    }

    public static Vector3fConverter newInstance() {
        return new Vector3fConverter();
    }
}
