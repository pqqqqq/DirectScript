package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

/**
 * Created by Kevin on 2015-06-18.
 * Represents a {@link DataContainer} in which both a {@link com.pqqqqq.directscript.lang.data.Literal} and its {@link LiteralHolder} can be resolved
 *
 * @param <T> the type parameter for the literal
 */
public interface HolderContainer<T> extends DataContainer<T> {

    /**
     * Resolves the {@link LiteralHolder} from this container in a particular {@link ScriptInstance}
     *
     * @param scriptInstance the script instance to resolve this container in
     * @return the {@link LiteralHolder}
     * @throws IllegalStateException if no literal can be resolved
     */
    LiteralHolder<T> resolveHolder(ScriptInstance scriptInstance);
}
