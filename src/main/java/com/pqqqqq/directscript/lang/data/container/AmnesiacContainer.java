package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.AmnesiacData;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-11-18.
 * An amnesiac {@link DataContainer} that re-resolves every time
 */
public class AmnesiacContainer<T> implements DataContainer<T> {
    private final DataContainer sequence;

    /**
     * Creates a new amnesiac container with the given data container to be re-resolved
     *
     * @param sequence the data container
     */
    public AmnesiacContainer(DataContainer<T> sequence) {
        this.sequence = sequence;
    }

    /**
     * Gets the {@link DataContainer} to be re-resolved
     *
     * @return the data container
     */
    public DataContainer<T> getSequence() {
        return sequence;
    }

    @Override
    public Datum<T> resolve(Context ctx) {
        return new AmnesiacData<T>(ctx, getSequence());
    }
}
