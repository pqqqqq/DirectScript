package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kevin on 2015-06-17.
 * An array statement that is a {@link List} of {@link DataContainer}s
 */
public class ArrayContainer implements DataContainer {
    private final List<DataContainer> list = new ArrayList<DataContainer>();

    /**
     * Creates an empty {@link ArrayContainer}
     */
    public ArrayContainer() {
    }

    /**
     * Creates a {@link ArrayContainer} with the given {@link DataContainer} {@link List}
     *
     * @param col the data container list
     */
    public ArrayContainer(Collection<? extends DataContainer> col) {
        this.list.addAll(col);
    }

    /**
     * Gets the {@link DataContainer} {@link List} for this {@link ArrayContainer}
     *
     * @return the data container list
     */
    public List<DataContainer> getList() {
        return list;
    }

    @Override
    public Literal<List<LiteralHolder>> resolve(ScriptInstance scriptInstance) {
        List<LiteralHolder> list = new ArrayList<LiteralHolder>();
        for (DataContainer dataContainer : getList()) {
            list.add(dataContainer.resolve(scriptInstance).toHolder());
        }

        return Literal.fromObject(list);
    }
}
