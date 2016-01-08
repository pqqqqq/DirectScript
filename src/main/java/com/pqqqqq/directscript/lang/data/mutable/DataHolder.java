package com.pqqqqq.directscript.lang.data.mutable;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link MutableValue} that mutably houses a {@link Datum} value
 */
public class DataHolder<C extends Datum<?>> implements MutableValue<C> {
    private C datum;

    /**
     * Creates a new {@link DataHolder} with a {@link com.pqqqqq.directscript.lang.data.Literal.Literals#EMPTY empty} value
     */
    public DataHolder() {
        this((C) Literal.Literals.EMPTY);
    }

    /**
     * Creates a new {@link DataHolder} with the given {@link Datum}
     *
     * @param datum the datum
     */
    public DataHolder(C datum) {
        this.datum = datum;
    }

    @Override
    public C getDatum() {
        return datum;
    }

    @Override
    public void setDatum(C datum) {
        // It is important to use a Datum's datum which it was resolved from, if present
        if (datum instanceof Literal) {
            Optional<Datum> resolvedFrom = ((Literal) datum).getResolvedFrom();
            if (resolvedFrom.isPresent()) {
                this.datum = (C) resolvedFrom.get();
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
