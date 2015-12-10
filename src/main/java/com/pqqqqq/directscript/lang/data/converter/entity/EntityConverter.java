package com.pqqqqq.directscript.lang.data.converter.entity;

import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-13.
 */
public class EntityConverter extends Converter<Entity> {
    public static final Map<Class, Function<Entity, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(EntityType.class, Entity::getType);
        CONVERSION_MAP.put(EntitySnapshot.class, Entity::createSnapshot);
        CONVERSION_MAP.put(UUID.class, Entity::getUniqueId);
        CONVERSION_MAP.put(String.class, (entity) -> entity.getType().getName());
    }

    private EntityConverter() {
        super(Entity.class, CONVERSION_MAP, Converters.DATA_HOLDER_CONVERTER);
        this.addInheritance(Converters.TRANSFORM_CONVERTER, Entity::getTransform);
    }

    public static EntityConverter newInstance() {
        return new EntityConverter();
    }
}
