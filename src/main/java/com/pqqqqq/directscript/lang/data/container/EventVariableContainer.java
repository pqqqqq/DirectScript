package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-11-14.
 * Represents a container that gets the event var from the data container
 */
public class EventVariableContainer implements DataContainer {
    private final DataContainer eventVar;

    /**
     * Creates a new {@link EventVariableContainer}
     *
     * @param eventVar the event var
     */
    public EventVariableContainer(DataContainer eventVar) {
        this.eventVar = eventVar;
    }

    /**
     * Gets the {@link DataContainer} containing the event variable's name
     *
     * @return the name container
     */
    public DataContainer getEventVar() {
        return eventVar;
    }

    @Override
    public Literal resolve(Context ctx) {
        String name = getEventVar().resolve(ctx).get().getString();
        return Literal.fromObject(ctx.getScriptInstance().getEventVars().get(name));
    }
}
