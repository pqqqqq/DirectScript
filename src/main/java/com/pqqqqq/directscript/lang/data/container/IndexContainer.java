package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that resolves a {@link HolderContainer} from a {@link ArrayContainer}
 */
public class IndexContainer implements HolderContainer {
    private final DataContainer array;
    private final DataContainer index;

    /**
     * Creates a new {@link IndexContainer} with the given {@link DataContainer} array and index
     *
     * @param array the array
     * @param index the index
     */
    public IndexContainer(DataContainer array, DataContainer index) {
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
        Literal arrayLiteral = getArray().resolve(scriptInstance);
        if (arrayLiteral.isMap()) {
            return arrayLiteral.getMapValue(getIndex().resolve(scriptInstance));
        } else {
            return arrayLiteral.getArrayValue(getIndex().resolve(scriptInstance).getNumber().intValue());
        }
    }
}
