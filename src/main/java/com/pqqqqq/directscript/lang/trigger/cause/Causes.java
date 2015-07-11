package com.pqqqqq.directscript.lang.trigger.cause;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.util.RegistryUtil;

import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a registry of {@link Cause}s
 */
public class Causes {

    // Compile
    public static final Cause COMPILE = new Cause("Compile");

    // Server states
    public static final Cause SERVER_STARTING = new Cause("ServerStarting");
    public static final Cause SERVER_STOPPING = new Cause("ServerStopping");

    // Misc.
    public static final Cause CALL = new Cause("Call");
    public static final Cause TIMER = new Cause.TimerCause();

    // Server events
    public static final Cause ENTITY_SPAWN = new Cause("Spawn", "EntitySpawn");

    // Player causes
    public static final Cause PLAYER_JOIN = new Cause("Join", "PlayerJoin");
    public static final Cause PLAYER_QUIT = new Cause("Quit", "PlayerQuit");
    public static final Cause PLAYER_DEATH = new Cause("Death", "PlayerDeath");
    public static final Cause PLAYER_CHAT = new Cause("Chat", "PlayerChat");
    public static final Cause PLAYER_CHANGE_HEALTH = new Cause("ChangeHealth", "PlayerChangeHealth");
    public static final Cause PLAYER_INTERACT_BLOCK = new Cause("InteractBlock");
    public static final Cause PLAYER_INTERACT_ENTITY = new Cause("InteractEntity");
    public static final Cause ITEM_PICKUP = new Cause("Pickup", "ItemPickup");
    public static final Cause ITEM_DROP = new Cause("Drop", "ItemDrop");
    public static final Cause BLOCK_PLACE = new Cause("Place", "BlockPlace");
    public static final Cause BLOCK_BREAK = new Cause("Break", "BlockBreak");
    public static final Cause COMMAND = new Cause.CommandCause();

    private static final List<Cause> REGISTRY;

    static {
        REGISTRY = RegistryUtil.getAllOf(Cause.class, Causes.class);
    }

    /**
     * Gets the {@link List} of {@link Cause}s in the registry
     *
     * @return the registry
     */
    public static List<Cause> getRegistry() {
        return REGISTRY;
    }

    /**
     * Gets an {@link Optional} {@link Cause} for the key
     *
     * @param key the key
     * @return the cause
     */
    public static Optional<Cause> getCause(String key) {
        key = key.trim().replace("_", "").replace(" ", "");
        for (Cause cause : REGISTRY) {
            if (cause.hasName(key)) {
                return Optional.of(cause);
            }
        }

        return Optional.absent();
    }
}
