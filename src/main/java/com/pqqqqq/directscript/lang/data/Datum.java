package com.pqqqqq.directscript.lang.data;

import com.pqqqqq.directscript.Config;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.data.mutable.DataHolder;

/**
 * Created by Kevin on 2015-11-19.
 * Represents a {@link Datum} that can be represented by a {@link Literal}
 */
public interface Datum<T> extends DataContainer<T> {

    /**
     * Serializes this {@link DataContainer} into a sequence, sequenced by {@link Sequencer} and saved in {@link Config}
     *
     * @return the sequence
     */
    Object serialize();

    /**
     * Converts this {@link DataContainer} into a {@link DataHolder}
     *
     * @return the new data holder
     */
    default DataHolder toHolder() {
        return new DataHolder(this);
    }
}
