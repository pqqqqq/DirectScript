package com.pqqqqq.directscript.lang.data;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Kevin on 2015-06-26.
 * <p>An abstract {@link Literal} that has an objective {@link com.google.common.base.Optional Optional} value and not a primitive one</p>
 * <p>This class abstractedly inherits {@link Literal#getString()} and nullifies other key methods</p>
 * <p>Overidden methods of Literal:</p>
 * <ul>
 * <li>{@link Literal#isString()}
 * <li>{@link Literal#isBoolean()}
 * <li>{@link Literal#isNumber()}
 * <li>{@link Literal#isArray()}
 * </ul>
 */
public abstract class ObjectiveLiteral<T> extends Literal<T> {

    ObjectiveLiteral(T value) {
        super(value);
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

        if (value instanceof Player) {
            return (ObjectiveLiteral<T>) new PlayerLiteral((Player) value);
        } else if (value instanceof World) {
            return (ObjectiveLiteral<T>) new WorldLiteral((World) value);
        } else if (value instanceof Item) {
            return (ObjectiveLiteral<T>) new ItemLiteral((Item) value);
        } else if (value instanceof ItemStack) {
            return (ObjectiveLiteral<T>) new ItemStackLiteral((ItemStack) value);
        } else if (value instanceof BlockSnapshot) {
            return (ObjectiveLiteral<T>) new BlockSnapshotLiteral((BlockSnapshot) value);
        } else if (value instanceof Location) {
            return (ObjectiveLiteral<T>) new LocationLiteral((Location) value);
        } else if (value instanceof Living) {
            return (ObjectiveLiteral<T>) new LivingLiteral((Living) value);
        } else if (value instanceof Entity) {
            return (ObjectiveLiteral<T>) new EntityLiteral((Entity) value);
        }

        return null;
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
    public ObjectiveLiteral<T> copy() {
        return this;
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link Player}
     */
    public static class PlayerLiteral extends LivingLiteral {

        PlayerLiteral(Player player) {
            super(player);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(Player.class)) {
                return (Optional<T>) getValue();
            }
            return super.getAs(type);
        }
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link World}
     */
    public static class WorldLiteral extends ObjectiveLiteral<World> {

        WorldLiteral(World world) {
            super(world);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(World.class)) {
                return (Optional<T>) getValue();
            }
            return super.getAs(type);
        }
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link Item}
     */
    public static class ItemLiteral extends EntityLiteral {

        ItemLiteral(Item item) {
            super(item);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(Item.class)) {
                return (Optional<T>) getValue();
            } else if (type.equals(ItemStack.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(((Item) getValue().get()).getItemData().getValue());
            }
            return super.getAs(type);
        }
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link ItemStack}
     */
    public static class ItemStackLiteral extends ObjectiveLiteral<ItemStack> {

        ItemStackLiteral(ItemStack itemStack) {
            super(itemStack);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(ItemStack.class)) {
                return (Optional<T>) getValue();
            }
            return super.getAs(type);
        }
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link BlockSnapshot}
     */
    public static class BlockSnapshotLiteral extends ObjectiveLiteral<BlockSnapshot> {

        BlockSnapshotLiteral(BlockSnapshot blockSnapshot) {
            super(blockSnapshot);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(BlockSnapshot.class)) {
                return (Optional<T>) getValue();
            } else if (type.equals(Vector3d.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(getValue().get().getLocation().toDouble());
            }
            return super.getAs(type);
        }
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link Location}
     */
    public static class LocationLiteral extends ObjectiveLiteral<Location> {

        LocationLiteral(Location location) {
            super(location);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(Location.class)) {
                return (Optional<T>) getValue();
            } else if (type.equals(World.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of((World) getValue().get().getExtent());
            } else if (type.equals(Vector3d.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(getValue().get().getPosition());
            } else if (type.equals(BlockSnapshot.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(getValue().get().getBlockSnapshot());
            }
            return super.getAs(type);
        }
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link Entity}
     */
    public static class EntityLiteral extends ObjectiveLiteral<Entity> {

        EntityLiteral(Entity value) {
            super(value);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(Entity.class)) {
                return (Optional<T>) getValue();
            } else if (type.equals(World.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(getValue().get().getWorld());
            } else if (type.equals(Location.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(getValue().get().getLocation());
            } else if (type.equals(Vector3d.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(getValue().get().getLocation().getPosition());
            } else if (type.equals(BlockSnapshot.class)) {
                return isEmpty() ? Optional.<T>absent() : (Optional<T>) Optional.of(getValue().get().getLocation().getBlockSnapshot());
            }
            return super.getAs(type);
        }
    }

    /**
     * An {@link ObjectiveLiteral} pertaining to a {@link Living}
     */
    public static class LivingLiteral extends EntityLiteral {

        LivingLiteral(Living value) {
            super(value);
        }

        @Override
        public <T> Optional<T> getAs(Class<T> type) {
            if (type.equals(Living.class)) {
                return (Optional<T>) getValue();
            }
            return super.getAs(type);
        }
    }
}
