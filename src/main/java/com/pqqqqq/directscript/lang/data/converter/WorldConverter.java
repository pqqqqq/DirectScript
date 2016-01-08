package com.pqqqqq.directscript.lang.data.converter;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.world.*;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.storage.WorldStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-14.
 */
public class WorldConverter extends Converter<World> {
    public static final Map<Class, Function<World, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(String.class, World::getName);
        CONVERSION_MAP.put(WorldCreationSettings.class, World::getCreationSettings);
        CONVERSION_MAP.put(Difficulty.class, World::getDifficulty);
        CONVERSION_MAP.put(Dimension.class, World::getDimension);
        CONVERSION_MAP.put(WorldProperties.class, World::getProperties);
        CONVERSION_MAP.put(TeleporterAgent.class, World::getTeleporterAgent);
        CONVERSION_MAP.put(WorldBorder.class, World::getWorldBorder);
        CONVERSION_MAP.put(WorldGenerator.class, World::getWorldGenerator);
        CONVERSION_MAP.put(WorldStorage.class, World::getWorldStorage);
        CONVERSION_MAP.put(Context.class, World::getContext);
        CONVERSION_MAP.put(UUID.class, World::getUniqueId);
    }

    private WorldConverter() {
        super(World.class, CONVERSION_MAP);
    }

    public static WorldConverter newInstance() {
        return new WorldConverter();
    }
}
