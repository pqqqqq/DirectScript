package com.pqqqqq.directscript.lang.data.container;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link Variable} {@link HolderContainer} which resolves the {@link Literal} value of a variable at runtime
 */
public class VariableContainer implements HolderContainer {
    private final String variableName;

    /**
     * Creates a new {@link VariableContainer} with the given {@link Variable}'s name
     *
     * @param variableName the variable's name
     */
    public VariableContainer(String variableName) {
        this.variableName = variableName;
    }

    /**
     * Gets the name of the variable for this container
     *
     * @return the variable name
     */
    public String getVariableName() {
        return variableName;
    }

    @Override
    public Literal resolve(ScriptInstance scriptInstance) {
        return resolveHolder(scriptInstance).getData();
    }

    @Override
    public LiteralHolder resolveHolder(ScriptInstance scriptInstance) {
        Optional<Variable> variableOptional = scriptInstance.getVariable(getVariableName());
        checkState(variableOptional.isPresent(), "Could not resolve symbol: " + variableName);

        return variableOptional.get();
    }
}
