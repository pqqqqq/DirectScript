package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

/**
 * Created by Kevin on 2015-06-17.
 * A simple {@link DataContainer} that negates {@link Literal}s, according to {@link Literal#negative()}
 */
public class NegateContainer implements DataContainer<Boolean> {
    private final DataContainer container;

    /**
     * Creates a new {@link NegateContainer} with the {@link DataContainer} to be negated
     *
     * @param container the container
     */
    public NegateContainer(DataContainer container) {
        this.container = container;
    }

    /**
     * Gets the {@link DataContainer} to be negated
     *
     * @return the data container
     */
    public DataContainer getContainer() {
        return container;
    }

    @Override
    public Literal<Boolean> resolve(ScriptInstance scriptInstance) {
        return getContainer().resolve(scriptInstance).negative();
    }
}
