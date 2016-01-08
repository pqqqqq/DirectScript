package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.data.mutable.ArrayIndexValue;
import com.pqqqqq.directscript.lang.data.mutable.DataHolder;
import com.pqqqqq.directscript.lang.data.mutable.MapIndexValue;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
import com.pqqqqq.directscript.lang.exception.IncompatibleTypeException;
import com.pqqqqq.directscript.lang.reader.Context;

import java.util.Optional;

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
    public Literal resolve(Context ctx) {
        Literal arrayLiteral = getArray().resolve(ctx);
        if (arrayLiteral.isMap()) {
            return arrayLiteral.or(Literal.Literals.EMPTY_MAP).getMapValue(getIndex().resolve(ctx));
        } else {
            return arrayLiteral.or(Literal.Literals.EMPTY_ARRAY).getArrayValue(getIndex().resolve(ctx).getNumber().intValue());
        }
    }

    @Override
    public MutableValue resolveValue(Context ctx) {
        // TODO : Does this interfere with amnesiac data?

        if (getArray() instanceof ValueContainer) {
            MutableValue mutableValue = ((ValueContainer) getArray()).resolveValue(ctx);
            if (mutableValue instanceof DataHolder) { // Indices are required to have field-membered data
                DataHolder dataHolder = (DataHolder) mutableValue;

                Literal index = getIndex().resolve(ctx);
                if (dataHolder instanceof Variable) {
                    Variable variable = (Variable) dataHolder;
                    Optional<Literal.Types> type = variable.getType();

                    if (type.isPresent()) {
                        switch (type.get()) {
                            case MAP:
                                return new MapIndexValue(dataHolder, index);
                            case ARRAY:
                                return new ArrayIndexValue(dataHolder, index.getNumber().intValue());
                            default:
                                throw new IncompatibleTypeException("%s cannot be indexed.", type.get().getName());
                        }
                    }
                }

                Literal arrayLiteral = dataHolder.getDatum().tryLiteral();
                if (arrayLiteral != null && arrayLiteral.isMap()) {
                    return new MapIndexValue(dataHolder, index);
                } else if (arrayLiteral != null && arrayLiteral.isArray()) {
                    return new ArrayIndexValue(dataHolder, index.getNumber().intValue());
                } else {
                    Literal getOrNull = index.tryLiteral();
                    if (getOrNull == null || !getOrNull.isNumber()) {
                        return new MapIndexValue(dataHolder, index);
                    } else {
                        return new ArrayIndexValue(dataHolder, index.getNumber().intValue());
                    }
                }
            }
        }

        return null;
    }
}
