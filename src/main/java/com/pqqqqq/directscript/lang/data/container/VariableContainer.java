package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.data.mutable.DataHolder;
import com.pqqqqq.directscript.lang.reader.Context;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link Variable} {@link ValueContainer} which resolves the {@link Datum} value of a variable at runtime
 */
public class VariableContainer implements ValueContainer {
    private final DataContainer variableName;

    /**
     * Creates a new {@link VariableContainer} with the given {@link Variable}'s name
     *
     * @param variableName the variable's name
     */
    public VariableContainer(DataContainer variableName) {
        this.variableName = variableName;
    }

    /**
     * Gets the {@link DataContainer} name of the variable for this container
     *
     * @return the variable name
     */
    public DataContainer getVariableName() {
        return variableName;
    }

    @Override
    public Datum resolve(Context ctx) {
        return resolveValue(ctx).getDatum();
    }

    @Override
    public DataHolder resolveValue(Context ctx) {
        Optional<Variable> variableOptional = ctx.getScriptInstance().getVariable(getVariableName().resolve(ctx).get().getString());
        checkState(variableOptional.isPresent(), "Could not resolve symbol: " + variableName);

        return variableOptional.get();
    }
}
