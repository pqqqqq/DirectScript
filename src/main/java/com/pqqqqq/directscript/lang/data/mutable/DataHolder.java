package com.pqqqqq.directscript.lang.data.mutable;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link MutableValue} that mutably houses a {@link Datum} value
 */
public class DataHolder implements MutableValue {
    private Datum datum;

    /**
     * Creates a new {@link DataHolder} with a {@link com.pqqqqq.directscript.lang.data.Literal.Literals#EMPTY empty} value
     */
    public DataHolder() {
        this(Literal.Literals.EMPTY);
    }

    /**
     * Creates a new {@link DataHolder} with the given {@link Datum}
     *
     * @param datum the datum
     */
    public DataHolder(Datum datum) {
        this.datum = datum;
    }

    @Override
    public <T> Datum<T> getDatum() {
        return datum;
    }

    @Override
    public void setDatum(Datum datum) {
        this.datum = datum;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DataHolder && datum.equals(((DataHolder) obj).getDatum());
    }
}
