package com.pqqqqq.directscript.lang.data.mutable;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;

/**
 * Created by Kevin on 2015-12-01.
 * <p>A mutable {@link Datum} entity.</p>
 * <p>This class differs from {@link DataHolder} in that it is not restricted to its datum being its own field member.</p>
 */
public interface MutableValue {

    /**
     * Gets the {@link Datum} for this {@link MutableValue}
     *
     * @return the datum
     */
    <T> Datum<T> getDatum();

    /**
     * Sets the {@link Datum} for this {@link Datum}
     *
     * @param datum the new datum
     */
    void setDatum(Datum datum);

    /**
     * Gets the {@link Literal} data for this {@link MutableValue}
     *
     * @return the literal
     */
    default <T> Literal<T> getLiteral() {
        return (Literal<T>) getDatum().get();
    }
}
