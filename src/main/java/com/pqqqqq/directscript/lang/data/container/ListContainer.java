package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kevin on 2015-06-17.
 * A statement that is a {@link List} of {@link DataContainer}s
 */
public class ListContainer implements DataContainer {
    private final List<DataContainer> list = new ArrayList<DataContainer>();

    /**
     * Creates an empty {@link ListContainer}
     */
    public ListContainer() {
    }

    /**
     * Creates a {@link ListContainer} with the given {@link DataContainer} {@link List}
     *
     * @param col the data container list
     */
    public ListContainer(Collection<? extends DataContainer> col) {
        this.list.addAll(col);
    }

    /**
     * Gets the {@link DataContainer} {@link List} for this {@link ListContainer}
     * @return the data container list
     */
    public List<DataContainer> getList() {
        return list;
    }

    public Literal<List<LiteralHolder>> resolve(ScriptInstance scriptInstance) {
        List<LiteralHolder> list = new ArrayList<LiteralHolder>();
        for (DataContainer dataContainer : getList()) {
            list.add(new LiteralHolder(dataContainer.resolve(scriptInstance)));
        }

        return Literal.getLiteralBlindly(list);
    }
}
