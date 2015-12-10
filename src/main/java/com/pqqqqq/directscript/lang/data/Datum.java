package com.pqqqqq.directscript.lang.data;

import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.data.mutable.DataHolder;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-11-19.
 * Represents a {@link Datum} that can be represented by a {@link Literal}
 */
public interface Datum<T> extends DataContainer<T> {

    /**
     * Gets the {@link Literal} for this {@link Datum}
     *
     * @return the literal
     */
    Literal<T> get();

    /**
     * Converts this {@link Datum} into a {@link DataHolder}
     *
     * @return the new literal holder
     */
    default DataHolder toHolder() {
        return new DataHolder(this);
    }

    @Override
    default Datum<T> resolve(Context ctx) {
        return this;
    }
}
