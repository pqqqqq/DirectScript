package com.pqqqqq.directscript.lang.container;

import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 * A piece of code that runs or does something when {@link Trigger}ed (link missing)
 */
public class Script {
    private final String name;
    private final List<Line> lines = new ArrayList<Line>();

    public Script(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Line> getLines() {
        return lines;
    }

    // Tentative method, run the container
    public void run(Cause fromCause) {
    }
}
