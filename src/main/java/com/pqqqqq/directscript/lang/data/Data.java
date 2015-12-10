package com.pqqqqq.directscript.lang.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kevin on 2015-11-19.
 * A {@link Datum} series that can be concatenated together
 */
public class Data implements Datum, Iterable<Datum> {
    private final List<Datum> data;
    private Literal simpleLiteral = null;

    /**
     * Creates a new {@link Data} instance with the given {@link Datum}
     *
     * @param data the data
     */
    public Data(Datum... data) {
        this.data = Arrays.asList(data);
    }

    /**
     * Gets the {@link Datum} {@link List} (data)
     *
     * @return the data
     */
    public List<Datum> getData() {
        return data;
    }

    /**
     * Gets the simple {@link Literal} if all {@link Datum} entries in this {@link Datum} are literals
     *
     * @return the simple literal, or null if not all datum are literals
     */
    public Literal getSimpleLiteral() {
        return simpleLiteral;
    }

    @Override
    public Literal get() {
        if (getSimpleLiteral() != null) {
            return getSimpleLiteral();
        }

        Literal result = null;
        boolean allLiterals = true;

        for (Datum datum : this) {
            if (!(datum instanceof Literal)) {
                allLiterals = false;
            }

            if (result == null) {
                result = datum.get(); // If there's no result yet, this is the first
            } else {
                result = result.add(datum.get()); // Otherwise add it to the pre-existing one
            }
        }

        if (allLiterals) {
            this.simpleLiteral = result;
        }

        return result;
    }

    @Override
    public Iterator<Datum> iterator() {
        return getData().iterator();
    }
}
