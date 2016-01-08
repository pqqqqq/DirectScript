package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link Variable} {@link ValueContainer} which resolves the {@link Literal} value of a variable at runtime
 */
public class VariableContainer implements ValueContainer {
    private final DataContainer variableName;
    private final boolean createNew;

    /**
     * Creates a new {@link VariableContainer} with the given {@link Variable}'s name that errors when no variable is given
     *
     * @param variableName the variable's name
     */
    public VariableContainer(DataContainer variableName) {
        this(variableName, false);
    }

    /**
     * Creates a new {@link VariableContainer} with the given {@link Variable}'s name that creates a new variable by default
     *
     * @param variableName the variable's name
     */
    public VariableContainer(DataContainer variableName, boolean createNew) {
        this.variableName = variableName;
        this.createNew = createNew;
    }

    /**
     * Gets the {@link DataContainer} name of the variable for this container
     *
     * @return the variable name
     */
    public DataContainer getVariableName() {
        return variableName;
    }

    /**
     * Gets if this container should create a new blank variable by default
     *
     * @return true if it should
     */
    public boolean doCreateNew() {
        return createNew;
    }

    @Override
    public Literal resolve(Context ctx) {
        Variable dataHolder = resolveValue(ctx);
        return dataHolder == null ? Literal.Literals.EMPTY : dataHolder.getDatum().resolve(ctx);
    }

    @Override
    public Variable resolveValue(Context ctx) {
        String name = getVariableName().resolve(ctx).getString();

        if (doCreateNew()) {
            return ctx.getScriptInstance().getOrCreate(name);
        } else {
            return ctx.getScriptInstance().getVariable(name).orElse(null);
        }
    }
}
