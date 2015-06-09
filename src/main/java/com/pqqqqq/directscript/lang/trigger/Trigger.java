package com.pqqqqq.directscript.lang.trigger;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

import javax.annotation.Nonnull;
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

    public static Builder builder() {
        return new Builder();
    }

    public Script getScript() {
        return script;
    }

    public Cause[] getCauses() {
        return causes;
    }

    public boolean hasCause(@Nonnull Cause cause) {
        checkNotNull(cause, "Cause cannot be null");
        for (Cause check : this.causes) {
            if (check.equals(cause)) {
                return true;
            }
        }

        return false;
    }

    public void trigger(ScriptInstance.Builder builder) {
        builder.copy().script(script).build().run(); // Each trigger needs a separate builder instance
    }

    public static class Builder {
        private Script script = null;
        private Set<Cause> causes = new HashSet<Cause>();

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
