package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-11-18.
 * An amnesiac {@link Datum} that re-resolves every time
 */
public class AmnesiacContainer<T> implements Datum<T> {
    private final DataContainer sequence;
    private final String stringSequence;

    private transient boolean firstRun = true;

    /**
     * Creates a new amnesiac container with the given data container to be re-resolved
     *
     * @param sequence the data container
     * @param stringSequence the string sequence
     */
    public AmnesiacContainer(DataContainer<T> sequence, String stringSequence) {
        this.sequence = sequence;
        this.stringSequence = stringSequence;
    }

    /**
     * Gets the {@link DataContainer} to be re-resolved
     *
     * @return the data container
     */
    public DataContainer<T> getSequence() {
        return sequence;
    }

    /**
     * Gets the string sequence used to make the data container
     *
     * @return the string sequence
     */
    public String getStringSequence() {
        return stringSequence;
    }

    @Override
    public Literal<T> resolve(Context ctx) {
        if (firstRun) {
            firstRun = false;
            return Literal.Resolved.fromObject(Literal.Literals.empty(), this);
        }

        return Literal.Resolved.fromObject(getSequence().resolve(ctx), this);
    }

    @Override
    public Object serialize() {
        return stringSequence;
    }
}
