package com.pqqqqq.directscript.lang.data.mutable;

import com.google.common.collect.Lists;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.util.Utilities;

import java.util.List;

/**
 * Created by Kevin on 2015-12-01.
 * <p>A {@link MutableValue} for an array index.</p>
 * <p>A {@link DataHolder} containing an array must be given as it requires a field-membered variable.</p>
 */
public class ArrayIndexValue implements MutableValue {
    private final DataHolder arrayHolder;
    private final int index;

    /**
     * Creates the {@link ArrayIndexValue} from the given {@link DataHolder} and index
     *
     * @param arrayHolder the array holder
     * @param index       the index
     */
    public ArrayIndexValue(DataHolder arrayHolder, int index) {
        this.arrayHolder = arrayHolder;
        this.index = index - 1; // Base 1 is done here
    }

    /**
     * Gets the {@link DataHolder} corresponding to the array
     *
     * @return the data holder
     */
    public DataHolder getArrayHolder() {
        return arrayHolder;
    }

    /**
     * Gets this array index's ordinal number
     *
     * @return the ordinal
     */
    public int getIndex() {
        return index;
    }

    @Override
    public <T> Datum<T> getDatum() {
        Literal arrayLiteral = getArrayHolder().getLiteral();

        List<Datum> literalHolders = Lists.newArrayList(arrayLiteral.getArray()); // We must create a new array that can be changed
        if (Utilities.buildToIndex(literalHolders, getIndex(), Literal.Literals.EMPTY)) {
            getArrayHolder().setDatum(Literal.fromObject(literalHolders)); // Update for shift
        }

        return literalHolders.get(getIndex());
    }

    @Override
    public void setDatum(Datum datum) {
        Literal arrayLiteral = getArrayHolder().getLiteral();

        List<Datum> literalHolders = Lists.newArrayList(arrayLiteral.getArray()); // We must create a new array that can be changed
        Utilities.buildToIndex(literalHolders, getIndex(), Literal.Literals.EMPTY);
        literalHolders.set(getIndex(), datum);

        getArrayHolder().setDatum(Literal.fromObject(literalHolders)); // Update
    }
}
