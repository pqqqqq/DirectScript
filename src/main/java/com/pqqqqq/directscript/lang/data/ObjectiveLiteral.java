package com.pqqqqq.directscript.lang.data;

import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.data.DataSerializable;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-26.
 * <p>A {@link Literal} that has an objective {@link Optional} value and not a primitive one</p>
 * <p>This class abstractedly inherits {@link Literal#getString()} and nullifies other key methods</p>
 * <p>Overidden methods of Literal:</p>
 * <ul>
 * <li>{@link Literal#isString()}
 * <li>{@link Literal#isBoolean()}
 * <li>{@link Literal#isNumber()}
 * <li>{@link Literal#isArray()}
 * <li>{@link Literal#isMap()}
 * <li>{@link Literal#isObjective()}
 * <li>{@link Literal#getAs(Class)}
 * </ul>
 */
public class ObjectiveLiteral<T> extends Literal<T> {
    private final Converter<T> objective;

    ObjectiveLiteral(T value, Converter<T> objective) {
        super(value);
        this.objective = objective;
    }

    /**
     * Creates a new {@link ObjectiveLiteral} from the given value
     *
     * @param value the non-null value
     * @param <T>   the type parameter of the value
     * @return the new objective literal
     */
    public static <T> ObjectiveLiteral<T> of(T value) {
        if (value == null) {
            return null;
        }

        Optional<Converter<T>> converter = Converters.fromType(value);
        if (!converter.isPresent()) {
            return null;
        }

        return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, converter.get());


        /*if (value instanceof Player) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, PlayerConverter.instance());
        } else if (value instanceof World) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, WorldConverter.instance());
        } else if (value instanceof Item) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, EntityConverter.ITEM_OBJECTIVE);
        } else if (value instanceof ItemStack) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, ItemStackConverter.instance());
        } else if (value instanceof Location) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, LocationConverter.instance());
        } else if (value instanceof Transform) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, TransformConverter.instance());
        } else if (value instanceof Living) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, LivingConverter.instance());
        } else if (value instanceof Entity) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, EntityConverter.instance());
        } else if (value instanceof UUID) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, Converter.UUID_OBJECTIVE);
        } else if (value instanceof BlockSnapshot) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, BlockSnapshotConverter.instance());
        } else if (value instanceof ItemStackSnapshot) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, ItemStackSnapshotConverter.instance());
        } else if (value instanceof BlockState) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, BlockStateConverter.instance());
        } else if (value instanceof EntitySnapshot) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, EntitySnapshotConverter.instance());
        } else if (value instanceof LocateableSnapshot) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, LocateableSnapshotConverter.instance());
        } else if (value instanceof DataHolder) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, DataSerializableConverter.DATA_HOLDER_OBJECTIVE);
        } else if (value instanceof DataSerializable) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, DataSerializableConverter.instance());
        } else if (value instanceof DataContainer) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, DataContainerConverter.instance());
        } else if (value instanceof CatalogType) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, CatalogTypeConverter.instance());
        } else if (value instanceof Text) {
            return (ObjectiveLiteral<T>) new ObjectiveLiteral(value, TextConverter.instance());
        }*/
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isMap() {
        return false;
    }

    @Override
    public boolean isObjective() {
        return true;
    }

    @Override
    public Object serialize() {
        if (getValue().isPresent()) {
            T value = getValue().get();
            if (value instanceof DataSerializable) {
                return ((DataSerializable) value).toContainer();
            } else {
                return value.toString();
            }
        } else {
            return ""; // Blank string for serialization
        }
    }

    @Override
    public <R> Optional<R> getAs(Class<R> type) {
        checkState(getValue().isPresent(), "Value is not present");

        // TODO List -> object?
        return objective.<R>convert(getValue().get(), type);
    }
}
