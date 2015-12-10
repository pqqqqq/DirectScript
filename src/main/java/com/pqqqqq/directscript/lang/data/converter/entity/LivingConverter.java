package com.pqqqqq.directscript.lang.data.converter.entity;

import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.Living;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * Created by Kevin on 2015-11-13.
 */
public class LivingConverter extends Converter<Living> {
    public static final Map<Class, Function<Living, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(HealthData.class, Living::getHealthData);
        CONVERSION_MAP.put(DamageableData.class, Living::getMortalData);
    }

    private LivingConverter() {
        super(Living.class, CONVERSION_MAP, Converters.ENTITY_CONVERTER);
    }

    public static LivingConverter newInstance() {
        return new LivingConverter();
    }
}
