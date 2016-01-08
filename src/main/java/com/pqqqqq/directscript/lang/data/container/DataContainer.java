package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a data container in which a {@link Literal} can be resolved in a given {@link Context}
 *
 * @param <T> the type parameter for the literal
 */
public interface DataContainer<T> {

    /**
     * Resolves the {@link Literal} from this container in a particular {@link Context}
     *
     * @param ctx the context to resolve this container in
     * @return the {@link Literal}
     */
    Literal<T> resolve(Context ctx);

    /**
     * Gets the {@link Datum} for this {@link DataContainer} only if this data container is a datum already
     *
     * @return the datum, or null if not an instance of one
     */
    default Datum<T> tryDatum() {
        return this instanceof Datum ? (Datum<T>) this : null;
    }

    /**
     * Gets the {@link Literal} for this {@link DataContainer} only if this data container is a literal already
     *
     * @return the literal, or null if not an instance of one
     */
    default Literal<T> tryLiteral() {
        return this instanceof Literal ? (Literal<T>) this : null;
    }
}
