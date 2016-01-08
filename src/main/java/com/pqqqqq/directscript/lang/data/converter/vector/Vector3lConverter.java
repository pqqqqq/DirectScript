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
public class Vector3lConverter extends Converter<Vector3l> {
    public static final Map<Class, Function<Vector3l, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Vector3i.class, Vector3l::toInt);
        CONVERSION_MAP.put(Vector3f.class, Vector3l::toFloat);
        CONVERSION_MAP.put(Vector3d.class, Vector3l::toDouble);
    }

    private Vector3lConverter() {
        super(Vector3l.class, CONVERSION_MAP);
    }

    public static Vector3lConverter newInstance() {
        return new Vector3lConverter();
    }
}
