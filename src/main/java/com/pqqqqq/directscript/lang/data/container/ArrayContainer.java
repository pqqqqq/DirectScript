package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kevin on 2015-06-17.
 * An array statement that is a {@link List} of {@link DataContainer}s
 */
public class ArrayContainer implements DataContainer<List<Literal>> {
    private final List<DataContainer> list = new ArrayList<>();
    private final boolean concatenate;

    /**
     * Creates an empty {@link ArrayContainer}
     */
    public ArrayContainer() {
        this.concatenate = false;
    }

    /**
     * Creates a {@link ArrayContainer} with the given {@link DataContainer} {@link List}
     *
     * @param col the data container list
     */
    public ArrayContainer(Collection<? extends DataContainer> col) {
        this(col, false);
    }

    /**
     * Creates a new {@link ArrayContainer} with the given {@link List} that will be concatenated if the resolved indices are arrays
     *
     * @param col         the data container list
     * @param concatenate whether to concatenate or not
     */
    public ArrayContainer(Collection<? extends DataContainer> col, boolean concatenate) {
        this.list.addAll(col);
        this.concatenate = concatenate;
    }

    /**
     * Gets the {@link Literal} {@link List} for this {@link ArrayContainer}
     *
     * @return the data container list
     */
    public List<DataContainer> getList() {
        return list;
    }

    @Override
    public Literal<List<Literal>> resolve(Context ctx) {
        List<Literal> list = new ArrayList<>();
        getList().forEach((dataContainer) -> {
            Literal literal = dataContainer.resolve(ctx);

            if (concatenate && literal.isArray()) {
                list.addAll(literal.getArray());
            } else {
                list.add(literal);
            }
        });
        return Literal.fromObject(list);
    }
}
