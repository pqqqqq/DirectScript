package com.pqqqqq.directscript.lang.trigger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a set of {@link Cause}s that prompt a {@link Script} to run
 */
public class Trigger {
    private final Script script;
    private final Cause[] causes;

    public static Builder builder() {
        return new Builder();
    }

    Trigger(Script script, Cause... causes) {
        this.script = script;
        this.causes = causes;
    }

    public Script getScript() {
        return script;
    }

    public Cause[] getCauses() {
        return causes;
    }

    public void trigger(Cause cause) {
        script.run(cause);
    }

    public static class Builder {
        private Script script = null;
        private Cause[] causes = null;

        Builder() { // Default visibility
        }

        public Builder script(Script script) {
            this.script = script;
            return this;
        }

        public Builder cause(Cause... causes) {
            this.causes = causes;
            return this;
        }

        public Trigger build() {
            checkNotNull(script, "Script cannot be null");
            checkState(causes != null && causes.length > 0, "At least one cause must be present");

            Trigger buildTrigger = new Trigger(script, causes);

            // Notify all causes of a new trigger
            for (Cause cause : causes) {
                cause.getTriggers().add(buildTrigger);
            }
            return buildTrigger;
        }
    }
}
