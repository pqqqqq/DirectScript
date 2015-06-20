package com.pqqqqq.directscript.lang.data;

import com.pqqqqq.directscript.lang.util.ICopyable;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a class that mutably houses a {@link Literal} value
 */
public class LiteralHolder<T> implements ICopyable<LiteralHolder<T>> {
    private Literal<T> data;

    /**
     * Creates a new {@link LiteralHolder} instance with {@link Literals#EMPTY} data
     */
    public LiteralHolder() {
        this(Literals.EMPTY);
    }

    /**
     * Creates a new {@link LiteralHolder} instance with the given data
     *
     * @param data the data
     */
    public LiteralHolder(Literal<T> data) {
        this.data = data;
    }

    /**
     * Gets the {@link Literal} data for this literal holder
     *
     * @return the data
     */
    public Literal getData() {
        return data;
    }

    /**
     * Sets the {@link Literal} data for this literal holder
     *
     * @param data the new data
     */
    public void setData(Literal data) {
        this.data = data;
    }

    @Override
    public LiteralHolder<T> copy() {
        return new LiteralHolder<T>(getData().copy());
    }
}
