package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-06-18.
 * Represents a {@link DataContainer} in which both {@link Literal} and its {@link MutableValue} can be resolved
 *
 * @param <T> the type parameter for the literal
 */
public interface ValueContainer<T> extends DataContainer<T> {

    /**
     * Resolves the {@link MutableValue} from this container in a particular {@link Context}
     *
     * @param ctx the context to resolve this container in
     * @return the {@link MutableValue}
     * @throws IllegalStateException if no literal can be resolved
     */
    MutableValue resolveValue(Context ctx);
}
