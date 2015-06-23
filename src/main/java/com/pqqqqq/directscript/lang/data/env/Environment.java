package com.pqqqqq.directscript.lang.data.env;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an abstract environment that holds {@link Variable}s
 */
public abstract class Environment implements Iterable<Variable> {
    private final Environment parent;
    private final Map<String, Variable> variableMap = new HashMap<String, Variable>();

    protected Environment() {
        this(null);
    }

    protected Environment(Environment parent) {
        this.parent = parent;
    }

    /**
     * Gets this {@link Environment}'s parent environment
     *
     * @return the parent
     */
    public Environment getParent() {
        return parent;
    }

    /**
     * Gets the {@link Map} of {@link Variable}s
     *
     * @return the map
     */
    public Map<String, Variable> getVariables() {
        return this.variableMap;
    }

    /**
     * Safely adds a new {@link Variable} to the variable {@link Map}
     *
     * @param variable the new variable to add
     * @return the variable
     */
    public Variable addVariable(Variable variable) {
        checkState(Variable.namePattern().matcher(variable.getName()).matches(), "This variable name (" + variable.getName() + ") has illegal characters (only alphanumeric/period and must start with alphabetic).");
        checkState(!Variable.illegalNames().matcher(variable.getName()).matches(), variable.getName() + " is an illegal name.");
        checkState(!getVariables().containsKey(variable.getName()), "A variable with this name already exists");

        getVariables().put(variable.getName(), variable);
        return variable;
    }

    /**
     * Gets a {@link Optional} {@link Variable} by its corresponding name, or checks its parent
     *
     * @param name the name of the variable
     * @return the variable
     */
    public Optional<Variable> getVariable(String name) {
        Optional<Variable> variableOptional = Optional.fromNullable(getVariables().get(name.trim()));

        if (getParent() != null && !variableOptional.isPresent()) {
            variableOptional = getParent().getVariable(name);
        }

        return variableOptional;
    }

    @Override
    public Iterator<Variable> iterator() {
        return this.variableMap.values().iterator();
    }
}
