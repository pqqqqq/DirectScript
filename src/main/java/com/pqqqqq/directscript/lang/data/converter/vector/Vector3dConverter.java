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
public class Vector3dConverter extends Converter<Vector3d> {
    public static final Map<Class, Function<Vector3d, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Vector3i.class, Vector3d::toInt);
        CONVERSION_MAP.put(Vector3f.class, Vector3d::toFloat);
        CONVERSION_MAP.put(Vector3l.class, Vector3d::toLong);
    }

    private Vector3dConverter() {
        super(Vector3d.class, CONVERSION_MAP);
    }

    public static Vector3dConverter newInstance() {
        return new Vector3dConverter();
    }
}
