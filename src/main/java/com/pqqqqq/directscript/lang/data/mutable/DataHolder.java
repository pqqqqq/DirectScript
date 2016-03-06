package com.pqqqqq.directscript.lang.data.mutable;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link MutableValue} that mutably houses a {@link Datum} value
 */
public class DataHolder<T, H extends Datum<T>> implements MutableValue<H> {
    private H datum;

    /**
     * Creates a new {@link DataHolder} with a {@link com.pqqqqq.directscript.lang.data.Literal.Literals#empty()} value
     */
    public DataHolder() {
        this((H) Literal.Literals.empty());
    }

    /**
     * Creates a new {@link DataHolder} with the given {@link Datum}
     *
     * @param datum the datum
     */
    public DataHolder(H datum) {
        this.datum = datum;
    }

    @Override
    public H getDatum() {
        return datum;
    }

    @Override
    public void setDatum(H datum) {
        // It is important to use a Datum's datum which it was resolved from, if present
        if (datum instanceof Literal) {
            Optional<Datum> resolvedFrom = ((Literal) datum).getResolvedFrom();
            if (resolvedFrom.isPresent()) {
                this.datum = (H) resolvedFrom.get();
                return; // Easier than two else statements with the same body
            }
        }

        this.datum = datum;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DataHolder && datum.equals(((DataHolder) obj).getDatum());
    }
}
