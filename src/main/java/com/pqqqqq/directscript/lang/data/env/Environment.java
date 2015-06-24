package com.pqqqqq.directscript.lang.data.env;

import com.google.common.base.Optional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an abstract environment that holds {@link Variable}s
 */
public abstract class Environment implements Iterable<Variable> {
    private final Environment parent;
    private final Set<Variable> variables = new HashSet<Variable>();

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
     * Gets the {@link Set} of {@link Variable}s
     *
     * @return the map
     */
    public Set<Variable> getVariables() {
        return this.variables;
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
        checkState(!getVariable(variable.getName()).isPresent(), "A variable with this name already exists");

        getVariables().add(variable);
        return variable;
    }

    /**
     * Gets a {@link Optional} {@link Variable} by its corresponding name, or checks its parent
     *
     * @param name the name of the variable
     * @return the variable
     */
    public Optional<Variable> getVariable(String name) {
        for (Variable variable : getVariables()) {
            if (variable.getName().equals(name)) {
                return Optional.of(variable);
            }
        }

        if (getParent() != null) {
            return getParent().getVariable(name);
        }

        return Optional.absent();
    }

    @Override
    public Iterator<Variable> iterator() {
        return this.variables.iterator();
    }
}
