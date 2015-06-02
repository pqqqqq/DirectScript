package com.pqqqqq.directscript.lang.container;

import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

/**
 * Created by Kevin on 2015-06-02.
 * A piece of code that runs or does something when {@link Trigger}ed (link missing)
 */
public class Script {
    private final String name;

    public Script(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Tentative method, run the container
    public void run(Cause fromCause) {
    }
}
