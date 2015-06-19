package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that resolves a {@link HolderContainer} from a {@link ArrayContainer}
 */
public class ArrayIndexContainer implements HolderContainer {
    private final DataContainer array;
    private final DataContainer index;

    /**
     * Creates a new {@link ArrayIndexContainer} with the given {@link DataContainer} array and index
     *
     * @param array the array
     * @param index the index
     */
    public ArrayIndexContainer(DataContainer array, DataContainer index) {
        this.array = array;
        this.index = index;
    }

    /**
     * Gets the {@link DataContainer} array
     *
     * @return the array
     */
    public DataContainer getArray() {
        return array;
    }

    /**
     * Gets the {@link DataContainer} index to get
     *
     * @return the index
     */
    public DataContainer getIndex() {
        return index;
    }

    @Override
    public Literal resolve(ScriptInstance scriptInstance) {
        return resolveHolder(scriptInstance).getData();
    }

    @Override
    public LiteralHolder resolveHolder(ScriptInstance scriptInstance) {
        return getArray().resolve(scriptInstance).getArrayValue(getIndex().resolve(scriptInstance).getNumber().intValue());
    }
}
