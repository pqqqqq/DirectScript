package com.pqqqqq.directscript.lang.trigger.cause;

import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.Trigger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin on 2015-06-02.
 * Represents something that can be identified as the cause of something (in game or otherwise)
 */
public class Cause {
    private final String name;
    private final Set<Trigger> triggers = new HashSet<Trigger>();

    Cause(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this cause
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the {@link Set} of {@link Trigger}s to be triggered with this cause is activated
     *
     * @return the trigger set
     */
    public Set<Trigger> getTriggers() {
        return triggers;
    }

    /**
     * Activates this trigger with no {@link com.pqqqqq.directscript.lang.script.ScriptInstance.Builder} active
     */
    public void activate() {
        activate(null);
    }

    /**
     * Activates this trigger with a specific {@link com.pqqqqq.directscript.lang.script.ScriptInstance.Builder}
     *
     * @param builder the builder
     */
    public void activate(ScriptInstance.Builder builder) {
        if (!triggers.isEmpty()) {
            if (builder == null) {
                builder = ScriptInstance.builder();
            }

            builder.cause(this);
            for (Trigger trigger : triggers) {
                trigger.trigger(builder);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
