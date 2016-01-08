package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link DataContainer} that, according to a boolean data container, returns a corresponding data container
 */
public class TernaryOperatorContainer implements DataContainer {
    private final DataContainer conditionContainer;
    private final DataContainer trueContainer;
    private final DataContainer falseContainer;

    /**
     * Creates a new {@link TernaryOperatorContainer} with its three components
     *
     * @param conditionContainer the condition container
     * @param trueContainer      the container corresponding to a true ternary operation
     * @param falseContainer     the container corresponding to a false ternary operation
     */
    public TernaryOperatorContainer(DataContainer conditionContainer, DataContainer trueContainer, DataContainer falseContainer) {
        this.conditionContainer = conditionContainer;
        this.trueContainer = trueContainer;
        this.falseContainer = falseContainer;
    }

    /**
     * Gets the {@link DataContainer} for the ternary condition
     *
     * @return the data container
     */
    public DataContainer getConditionContainer() {
        return conditionContainer;
    }

    /**
     * Gets the {@link DataContainer} for the true ternary operation
     *
     * @return the data container
     */
    public DataContainer getTrueContainer() {
        return trueContainer;
    }

    /**
     * Gets the {@link DataContainer} for the false ternary operation
     *
     * @return the data container
     */
    public DataContainer getFalseContainer() {
        return falseContainer;
    }

    @Override
    public Literal resolve(Context ctx) {
        Literal condition = getConditionContainer().resolve(ctx);
        return condition.getBoolean() ? getTrueContainer().resolve(ctx) : getFalseContainer().resolve(ctx); // Use a ternary operator for the ternary operator... inception
    }
}
