package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a data container in which a {@link Literal} can be resolved in a given {@link com.pqqqqq.directscript.lang.script.ScriptInstance}
 *
 * @param <T> the type parameter for the literal
 */
public interface DataContainer<T> {

    /**
     * Resolves the {@link Literal} from this container in a particular {@link ScriptInstance}
     *
     * @param scriptInstance the script instance to resolve this container in
     * @return the {@link Literal}
     */
    Literal<T> resolve(ScriptInstance scriptInstance);
}
