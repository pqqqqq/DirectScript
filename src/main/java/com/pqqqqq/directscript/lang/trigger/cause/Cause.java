package com.pqqqqq.directscript.lang.trigger.cause;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.env.Variable;
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

        CommandCause() {
            super("Command");
        }

        @Override
        public boolean accepts(ScriptInstance scriptInstance) {
            String[] aliases = scriptInstance.getScript().getCauseData().getCommandAliases();
            if (aliases == null) {
                return false;
            }

            Optional<Variable> commandExec = scriptInstance.getEnvironment().getVariable("sponge.command");
            if (commandExec.isPresent()) {
                String comp = commandExec.get().getData().getString();
                for (String alias : aliases) {
                    if (alias.equalsIgnoreCase(comp)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
