package com.pqqqqq.directscript.lang.trigger.cause;

import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.Trigger;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 * Represents something that can be identified as the cause of something (in game or otherwise)
 */
public class Cause {
    private final String[] names;
    private final Set<Trigger> triggers = new HashSet<Trigger>();

    Cause(String... names) {
        this.names = checkNotNull(names, "Name");
    }

    /**
     * Gets the names of this cause
     *
     * @return the name
     */
    public String[] getNames() {
        return names;
    }

    /**
     * Checks if this cause has the given name
     *
     * @param name the name to check
     * @return true if it has the name
     */
    public boolean hasName(String name) {
        for (String nm : names) {
            if (nm.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
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
                ScriptInstance buildInstance = builder.copy().script(trigger.getScript()).build();
                if (accepts(buildInstance)) {
                    buildInstance.execute();
                }
            }
        }
    }

    /**
     * Returns if this {@link Cause} accepts the given {@link ScriptInstance}. This returns true unless overriden
     *
     * @param scriptInstance the script instance
     * @return true if accepted
     */
    public boolean accepts(ScriptInstance scriptInstance) {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    // Custom causes

    /**
     * Represents a custom timer cause
     */
    public static class TimerCause extends Cause {

        TimerCause() {
            super("Timer");
        }

        @Override
        public boolean accepts(ScriptInstance scriptInstance) {
            Long timerDelay = scriptInstance.getScript().getCauseData().getTimerDelay();
            Long lastTimeRun = scriptInstance.getScript().getCauseData().getLastTimerRun();

            if (timerDelay == null) { // This should never happen...
                return false;
            }

            if (lastTimeRun == null || (System.currentTimeMillis() - lastTimeRun) >= timerDelay) {
                scriptInstance.getScript().getCauseData().setLastTimerRun();
                return true;
            }
            return false;
        }
    }

    /**
     * Represents a custom command cause
     */
    public static class CommandCause extends Cause {

        CommandCause(String name) {
            super(name);
        }

        @Override
        public boolean accepts(ScriptInstance scriptInstance) {
            String[] aliases = scriptInstance.getScript().getCauseData().getCommandAliases();
            if (aliases == null) {
                return false;
            }

            Object cmd = scriptInstance.getEventVars().get("Command");
            if (cmd != null && cmd instanceof String) {
                String cmdString = (String) cmd;

                for (String alias : aliases) {
                    if (alias.equalsIgnoreCase(cmdString)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
