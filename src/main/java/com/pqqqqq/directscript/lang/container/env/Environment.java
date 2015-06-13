package com.pqqqqq.directscript.lang.container.env;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.variable.IVariableContainer;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.util.StringParser;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an environment that holds {@link Variable}s
 */
public class Environment implements IVariableContainer {
    private final ScriptInstance scriptInstance;
    private final Map<String, Variable> variableMap = new HashMap<String, Variable>();

    /**
     * Creates a new Environment for this {@link ScriptInstance}
     *
     * @param scriptInstance the script instance
     */
    public Environment(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
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
        checkState(Variable.namePattern().matcher(variable.getName()).matches(), "This variable name has illegal characters (only alphanumeric/period and must start with alphabetic).");
        checkState(!getVariables().containsKey(variable.getName()), "A variable with this name already exists");

        getVariables().put(variable.getName(), variable);
        return variable;
    }

    /**
     * Gets a {@link Optional} {@link Variable} by its corresponding name
     *
     * @param name the name of the variable
     * @return the variable
     */
    public Optional<Variable> getVariable(String name) {
        int openBracket = name.indexOf('[');
        String noBracketName = name.substring(0, (openBracket == -1 ? name.length() : openBracket));

        return Optional.fromNullable(getArrayValue(name.trim(), getVariables().get(noBracketName.trim())));
    }

    private Variable getArrayValue(String name, Variable variable) {
        if (variable == null) {
            return null;
        }

        String bracket = StringParser.instance().getOuterBracket(name, '[', ']');
        if (bracket != null) {
            int index = scriptInstance.getSequencer().parse(bracket.substring(1, bracket.length() - 1)).getNumber().intValue() - 1; // Minus one cause it starts at 1
            return getArrayValue(name.replace(bracket, ""), variable.getData().getArrayValue(index));
        }
        return variable;
    }
}
