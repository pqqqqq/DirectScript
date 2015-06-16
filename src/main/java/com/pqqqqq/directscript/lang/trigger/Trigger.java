package com.pqqqqq.directscript.lang.trigger;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a set of {@link Cause}s that prompt a {@link Script} to run
 */
public class Trigger {
    private final Script script;
    private final Cause[] causes;

    Trigger(Script script, Cause... causes) {
        this.script = script;
        this.causes = causes;

        this.script.setTrigger(Optional.of(this));
    }

    /**
     * Gets a builder instance for building {@link Trigger}s
     *
     * @return a builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets the {@link Script} associated with this trigger
     *
     * @return the script
     */
    public Script getScript() {
        return script;
    }

    /**
     * Gets the {@link Cause} array for this trigger
     *
     * @return the cause array
     */
    public Cause[] getCauses() {
        return causes;
    }

    /**
     * Gets whether this trigger has the specified {@link Cause}
     *
     * @param cause the cause to check
     * @return true if the cause is present in the array
     */
    public boolean hasCause(Cause cause) {
        checkNotNull(cause, "Cause cannot be null");
        for (Cause check : this.causes) {
            if (check.equals(cause)) {
                return true;
            }
        }

        return false;
    }

    /**
     * The builder for {@link Trigger}s
     */
    public static class Builder {
        private Script script = null;
        private Set<Cause> causes = new HashSet<Cause>();

        Builder() { // Default visibility
        }

        /**
         * Sets the {@link Script} for this trigger
         *
         * @param script the new script
         * @return this builder, for fluency
         * @see Trigger#getScript()
         */
        public Builder script(Script script) {
            this.script = script;
            return this;
        }

        /**
         * Sets the {@link Cause} array for this trigger
         *
         * @param causes the new cause array
         * @return this builder, for fluency
         * @see Trigger#getCauses()
         */
        public Builder cause(Cause... causes) {
            this.causes.addAll(Arrays.asList(causes));
            return this;
        }

        /**
         * Builds the new {@link Trigger} instance, and notifies all of its {@link Cause}s
         * @return the new trigger
         */
        public Trigger build() {
            checkNotNull(script, "Script cannot be null");
            checkState(!causes.isEmpty(), "At least one cause must be present");

            Trigger buildTrigger = new Trigger(script, causes.toArray(new Cause[causes.size()]));

            // Notify all causes of a new trigger
            for (Cause cause : causes) {
                cause.getTriggers().add(buildTrigger);
            }
            return buildTrigger;
        }
    }
}
