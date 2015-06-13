package com.pqqqqq.directscript.lang.data.variable;

import com.google.common.base.Optional;

import java.util.Map;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an interface that holds {@link Variable}s
 */
public interface IVariableContainer {

    /**
     * Add a {@link Variable} to this container
     *
     * @param variable the variable to add
     * @return the variable
     */
    Variable addVariable(Variable variable);

    /**
     * Gets a {@link Map} of Variable name vs. {@link Variable}
     *
     * @return the map
     */
    Map<String, Variable> getVariables();

    /**
     * Gets an {@link Optional} {@link Variable} by its corresponding name
     *
     * @param name the name to check
     * @return the variable
     */
    Optional<Variable> getVariable(String name);
}
