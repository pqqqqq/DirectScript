package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-07-21.
 * A {@link DataContainer} that cannot be resolved
 */
public class UnresolvableContainer<T> implements DataContainer<T> {
    private DataContainer<T> dataContainer;

    /**
     * Creates a new {@link DataContainer} with the given container
     *
     * @param dataContainer the container
     */
    public UnresolvableContainer(DataContainer<T> dataContainer) {
        this.dataContainer = dataContainer;
    }

    /**
     * Gets the {@link DataContainer} that this {@link UnresolvableContainer} is blocking the resolution of
     *
     * @return the data container
     */
    public DataContainer<T> getDataContainer() {
        return dataContainer;
    }

    @Override
    public Literal<T> resolve(Context ctx) {
        return Literal.Literals.EMPTY;
    }
}
