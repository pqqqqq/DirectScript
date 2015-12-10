package com.pqqqqq.directscript.lang.data.converter;

import com.pqqqqq.directscript.lang.data.converter.entity.EntityConverter;
import com.pqqqqq.directscript.lang.data.converter.entity.LivingConverter;
import com.pqqqqq.directscript.lang.data.converter.entity.PlayerConverter;
import com.pqqqqq.directscript.lang.data.converter.snapshot.BlockSnapshotConverter;
import com.pqqqqq.directscript.lang.data.converter.snapshot.EntitySnapshotConverter;
import com.pqqqqq.directscript.lang.data.converter.snapshot.ItemStackSnapshotConverter;
import com.pqqqqq.directscript.lang.data.converter.snapshot.LocateableSnapshotConverter;
import com.pqqqqq.directscript.lang.util.RegistryUtil;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.entity.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Kevin on 2015-11-16.
 * A list of converters
 */
public class Converters {

    // IMPORTANT: THE ORDER OF THESE ENTRIES MATTERS

    // Data
    public static final DataSerializableConverter DATA_SERIALIZABLE_CONVERTER = DataSerializableConverter.newInstance();
    public static final Converter<DataHolder> DATA_HOLDER_CONVERTER = new Converter<>(DataHolder.class, DATA_SERIALIZABLE_CONVERTER); // Must be after DataSerialiable
    public static final DataContainerConverter DATA_CONTAINER_CONVERTER = DataContainerConverter.newInstance();

    // Misc.
    public static final WorldConverter WORLD_CONVERTER = WorldConverter.newInstance();
    public static final BlockStateConverter BLOCK_STATE_CONVERTER = BlockStateConverter.newInstance(); // Must be after DataSerializable
    public static final LocationConverter LOCATION_CONVERTER = LocationConverter.newInstance(); // Must be after World and BlockState

    public static final CatalogTypeConverter CATALOG_TYPE_CONVERTER = CatalogTypeConverter.newInstance();
    public static final ItemStackConverter ITEM_STACK_CONVERTER = ItemStackConverter.newInstance(); // Must be after DataHolder
    public static final TextConverter TEXT_CONVERTER = TextConverter.newInstance();
    public static final TransformConverter TRANSFORM_CONVERTER = TransformConverter.newInstance(); // Must be after location
    public static final Converter<UUID> UUID_CONVERTER = new Converter<>(UUID.class);

    // Entities
    public static final EntityConverter ENTITY_CONVERTER = EntityConverter.newInstance(); // Must be after DataHolder and Transform
    public static final LivingConverter LIVING_CONVERTER = LivingConverter.newInstance(); // Must be after entity
    public static final PlayerConverter PLAYER_CONVERTER = PlayerConverter.newInstance(); // Must be after living
    public static final Converter<Item> ITEM_CONVERTER = new Converter<>(Item.class, ENTITY_CONVERTER); // Must be after entity

    // Snapshots
    public static final LocateableSnapshotConverter LOCATEABLE_SNAPSHOT_CONVERTER = LocateableSnapshotConverter.newInstance(); // Must be after DataSerializable and Location
    public static final BlockSnapshotConverter BLOCK_SNAPSHOT_CONVERTER = BlockSnapshotConverter.newInstance(); // Must be after BlockState and LocateableSnapshot
    public static final EntitySnapshotConverter ENTITY_SNAPSHOT_CONVERTER = EntitySnapshotConverter.newInstance(); // Must be after Entity and LocateableSnapshot
    public static final ItemStackSnapshotConverter ITEM_STACK_SNAPSHOT_CONVERTER = ItemStackSnapshotConverter.newInstance(); // Must be after ItemStack

    private static final List<Converter> REGISTRY;

    static {
        REGISTRY = RegistryUtil.getAllOf(Converter.class, Converters.class);
    }

    /**
     * Gets the {@link List} of {@link Converter}s in the registry
     *
     * @return the registry
     */
    public static List<Converter> getRegistry() {
        return REGISTRY;
    }

    /**
     * Gets an {@link Optional} {@link Converter} for the key
     *
     * @param key the key
     * @return the cause
     */
    public static Optional<Converter> fromName(String key) {
        key = key.trim().replace("_", "").replace(" ", "");
        for (Converter converter : REGISTRY) {
            if (converter.getName().equalsIgnoreCase(key)) {
                return Optional.of(converter);
            }
        }

        return Optional.empty();
    }

    public static <T> Optional<Converter<T>> fromClass(Class<T> clazz) {
        for (Converter converter : REGISTRY) {
            if (converter.getGenericClass().equals(clazz)) {
                return Optional.of(converter);
            }
        }

        return Optional.empty();
    }
}
