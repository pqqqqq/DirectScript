package com.pqqqqq.directscript.lang.trigger.cause;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.Trigger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin on 2015-06-02.
 * Represents something that can be identified as the cause of something (in game or otherwise)
 */
public class Cause {
    private final String cause;
    private final Set<Trigger> triggers = new HashSet<Trigger>();

    public Cause(String cause) {
        this.cause = cause;
    }

    public String getCause() {
        return cause;
    }

    public Set<Trigger> getTriggers() {
        return triggers;
    }

    public void trigger() {
        trigger(ScriptInstance.builder());
    }

    public void trigger(ScriptInstance.Builder builder) {
        builder.cause(this);

        for (Trigger trigger : triggers) {
            trigger.trigger(builder);
        }
    }
}
