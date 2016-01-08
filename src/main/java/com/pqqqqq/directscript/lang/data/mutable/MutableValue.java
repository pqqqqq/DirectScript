package com.pqqqqq.directscript.lang.data.mutable;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-12-01.
 * <p>A mutable {@link Datum} entity.</p>
 * <p>This class differs from {@link DataHolder} in that it is not restricted to its datum being its own field member.</p>
 */
public interface MutableValue<C extends Datum<?>> {

    /**
     * Gets the {@link Datum} for this {@link MutableValue}
     *
     * @return the datum
     */
    C getDatum();

    /**
     * Sets the {@link Datum} for this {@link MutableValue}
     *
     * @param datum the new datum
     */
    void setDatum(C datum);

    /**
     * Gets the {@link Literal} data for this {@link MutableValue}
     *
     * @return the literal
     */
    default Literal<?> resolve(Context ctx) {
        return getDatum().resolve(ctx);
    }
}
