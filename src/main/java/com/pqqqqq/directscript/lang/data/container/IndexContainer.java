package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.mutable.ArrayIndexValue;
import com.pqqqqq.directscript.lang.data.mutable.DataHolder;
import com.pqqqqq.directscript.lang.data.mutable.MapIndexValue;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that resolves a {@link ValueContainer} from a {@link ArrayContainer}
 */
public class IndexContainer implements ValueContainer {
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
    public Datum resolve(Context ctx) {
        Literal arrayLiteral = getArray().resolve(ctx).get();
        if (arrayLiteral.isMap()) {
            return arrayLiteral.or(Literal.Literals.EMPTY_MAP).getMapValue(getIndex().resolve(ctx).get());
        } else {
            return arrayLiteral.or(Literal.Literals.EMPTY_ARRAY).getArrayValue(getIndex().resolve(ctx).get().getNumber().intValue());
        }
    }

    @Override
    public MutableValue resolveValue(Context ctx) {
        // TODO : Does this interfere with amnesiac data?

        if (getArray() instanceof ValueContainer) {
            MutableValue mutableValue = ((ValueContainer) getArray()).resolveValue(ctx);
            if (mutableValue instanceof DataHolder) { // Indices are required to have field-membered data
                DataHolder dataHolder = (DataHolder) mutableValue;
                Literal arrayLiteral = dataHolder.getLiteral();

                if (arrayLiteral.isMap()) {
                    return new MapIndexValue(dataHolder, getIndex().resolve(ctx));
                } else {
                    return new ArrayIndexValue(dataHolder, getIndex().resolve(ctx).get().getNumber().intValue());
                }
            }
        }

        return null;
    }
}
