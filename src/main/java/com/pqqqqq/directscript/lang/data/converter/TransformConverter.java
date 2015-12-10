package com.pqqqqq.directscript.lang.data.converter;

import com.flowpowered.math.matrix.Matrix4d;
import org.spongepowered.api.entity.Transform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-13.
 */
public class TransformConverter extends Converter<Transform> {
    public static final Map<Class, Function<Transform, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(Matrix4d.class, Transform::toMatrix);
    }

    private TransformConverter() {
        super(Transform.class, CONVERSION_MAP);
        this.addInheritance(Converters.LOCATION_CONVERTER, Transform::getLocation);
    }

    public static TransformConverter newInstance() {
        return new TransformConverter();
    }
}
