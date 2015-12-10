package com.pqqqqq.directscript.lang.data.env;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an abstract environment that holds {@link Variable}s
 */
public abstract class Environment implements Iterable<Variable> {
    private final Environment parent;
    private final Set<Variable> variables = new HashSet<>();
    boolean suppressNotifications = false;
    private Environment daughter = null;

    protected Environment() {
        this(null);
    }

    protected Environment(Environment parent) {
        this.parent = parent;

        if (this.parent != null) {
            this.parent.setDaughter(this);
        }
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
     * Gets this {@link Environment}'s daughter
     * @return the daughter
     */
    public Environment getDaughter() {
        return daughter;
    }

    void setDaughter(Environment daughter) {
        this.daughter = daughter;
    }

    /**
     * Safely adds a new {@link Variable} to the variable {@link Set}
     *
     * @param variable the new variable to add
     * @return the variable
     */
    public Variable addVariable(Variable variable) {
        synchronized (variables) {
            checkState(Variable.namePattern().matcher(variable.getName()).matches(), "This variable name (" + variable.getName() + ") has illegal characters (only alphanumeric/period and must start with alphabetic).");
            checkState(!Variable.illegalNames().matcher(variable.getName()).matches(), variable.getName() + " is an illegal name.");

            //if (getVariableHere(variable.getName()).isPresent()) {
            //    return null;
            //}

            this.variables.add(variable);
            if (!suppressNotifications) {
                notifyChange();
            }

            return variable;
        }
    }

    /**
     * Gets a {@link Optional} {@link Variable} by its corresponding name, or checks its parent and daughters
     *
     * @param name the name of the variable
     * @return the variable
     */
    public Optional<Variable> getVariable(String name) {
        return getTop().getVariableLoad(name);
    }

    Optional<Variable> getVariableLoad(String name) {
        Optional<Variable> result = getVariableHere(name);

        if (!result.isPresent() && parent != null) {
            return getParent().getVariableLoad(name);
        }

        return result;
    }

    Optional<Variable> getVariableHere(String name) {
        synchronized (variables) {
            for (Variable variable : this) {
                if (variable.getName().equals(name)) {
                    return Optional.of(variable);
                }
            }

            return Optional.empty();
        }
    }

    /**
     * Removes a {@link Variable} from this environment by its name, or checks its parent
     *
     * @param name the name of the variable
     * @return true if the variable was removed
     */
    public boolean removeVariable(String name) {
        return getTop().removeVariableLoad(name);
    }

    boolean removeVariableLoad(String name) {
        synchronized (variables) {
            if (variables.removeIf((variable) -> variable.getName().equals(name))) {
                if (!suppressNotifications) {
                    notifyChange();
                }
                return true;
            }

            if (getParent() != null) {
                return getParent().removeVariableLoad(name);
            }

            return false;
        }
    }

    /**
     * Clears all {@link Variable}s in this environment
     */
    public void clear() {
        synchronized (variables) {
            this.variables.clear();
        }
    }

    /**
     * Gets the top of the {@link Environment} chain
     *
     * @return the top
     */
    public Environment getTop() {
        if (daughter != null) {
            return daughter.getTop();
        }
        return this;
    }

    /**
     * Gets the bottom of the {@link Environment} chain
     *
     * @return the bottom
     */
    public Environment getBottom() {
        if (parent != null) {
            return parent.getBottom();
        }
        return this;
    }

    protected void notifyChange() {
    }

    protected void suppressNotifications(boolean suppressNotifications) {
        this.suppressNotifications = suppressNotifications;
    }

    @Override
    public Iterator<Variable> iterator() {
        return this.variables.iterator();
    }
}
