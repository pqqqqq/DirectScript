package com.pqqqqq.directscript.lang.trigger;

import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    }

    public static Builder builder() {
        return new Builder();
    }

    public Script getScript() {
        return script;
    }

    public Cause[] getCauses() {
        return causes;
    }

    public void trigger(ScriptInstance.Builder builder) {
        script.run(builder.copy().script(script).build()); // Each trigger needs a separate builder instance
    }

    public static class Builder {
        private Script script = null;
        private List<Cause> causes = new ArrayList<Cause>();

        Builder() { // Default visibility
        }

        public Builder script(Script script) {
            this.script = script;
            return this;
        }

        public Builder cause(Cause... causes) {
            this.causes.addAll(Arrays.asList(causes));
            return this;
        }

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
