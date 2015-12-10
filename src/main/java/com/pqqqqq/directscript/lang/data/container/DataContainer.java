package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a data container in which a {@link Datum} can be resolved in a given {@link Context}
 *
 * @param <T> the type parameter for the literal
 */
public interface DataContainer<T> {

    /**
     * Resolves the {@link Datum} from this container in a particular {@link Context}
     *
     * @param ctx the context to resolve this container in
     * @return the {@link Datum}
     */
    Datum<T> resolve(Context ctx);
}
