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
public class Vector3iConverter extends Converter<Vector3i> {
    public static final Map<Class, Function<Vector3i, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Vector3d.class, Vector3i::toDouble);
        CONVERSION_MAP.put(Vector3f.class, Vector3i::toFloat);
        CONVERSION_MAP.put(Vector3l.class, Vector3i::toLong);
    }

    private Vector3iConverter() {
        super(Vector3i.class, CONVERSION_MAP);
    }

    public static Vector3iConverter newInstance() {
        return new Vector3iConverter();
    }
}
