package com.pqqqqq.directscript.lang.data;

import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-11-19.
 * A Datum ({@link Datum}) that must re-resolves to retrieve its {@link Literal}
 */
public class AmnesiacData<T> implements Datum<T> {
    private final Context ctx;
    private final DataContainer<T> dataContainer;

    /**
     * Creates a new {@link AmnesiacData}
     *
     * @param ctx           the {@link Context} used to resolve
     * @param dataContainer the {@link DataContainer} used to resolve
     */
    public AmnesiacData(Context ctx, DataContainer<T> dataContainer) {
        this.ctx = ctx;
        this.dataContainer = dataContainer;
    }

    /**
     * Gets the {@link Context}
     *
     * @return the context
     */
    public Context getContext() {
        return ctx;
    }

    /**
     * Gets the {@link DataContainer}
     *
     * @return the data container
     */
    public DataContainer<T> getDataContainer() {
        return dataContainer;
    }

    @Override
    public Literal<T> get() {
        return getDataContainer().resolve(getContext()).get();
    }
}
