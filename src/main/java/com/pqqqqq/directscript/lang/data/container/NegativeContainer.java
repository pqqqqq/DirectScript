package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-08-04.
 * A simple {@link DataContainer} that gets the negative of a number {@link Literal}s, according to {@link Literal#negative()}
 */
public class NegativeContainer implements DataContainer<Double> {
    private final DataContainer container;

    /**
     * Creates a new {@link NegativeContainer} with the {@link DataContainer} to get the negative of
     *
     * @param container the container
     */
    public NegativeContainer(DataContainer container) {
        this.container = container;
    }

    /**
     * Gets the {@link DataContainer} to get the negative of
     *
     * @return the data container
     */
    public DataContainer getContainer() {
        return container;
    }

    @Override
    public Literal<Double> resolve(Context ctx) {
        return getContainer().resolve(ctx).get().negative();
    }
}
