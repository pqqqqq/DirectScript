package com.pqqqqq.directscript.lang.trigger.cause;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.util.RegistryUtil;

import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Causes {

    // Compile
    public static final Cause COMPILE = new Cause("Compile");

    // Server states
    public static final Cause SERVER_STARTING = new Cause("ServerStarting");
    public static final Cause SERVER_STOPPING = new Cause("ServerStopping");

    // Misc.
    public static final Cause CALL = new Cause("Call");

    // Player causes
    public static final Cause PLAYER_JOIN = new Cause("PlayerJoin");

    private static final List<Cause> REGISTRY;
    static {
        REGISTRY = RegistryUtil.getAllOf(Cause.class, Causes.class);
    }

    public static List<Cause> getRegistry() {
        return REGISTRY;
    }

    public static Optional<Cause> getCause(String key) {
        for (Cause cause : REGISTRY) {
            if (key.trim().replace("_", "").replace(" ", "").equalsIgnoreCase(cause.getCause().trim().replace("_", "").replace(" ", ""))) {
                return Optional.of(cause);
            }
        }

        return Optional.absent();
    }
}
