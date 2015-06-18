package com.pqqqqq.directscript.lang.data.env;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an environment that holds {@link Variable}s
 */
public class Environment implements Iterable<Variable> {
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
        return Optional.fromNullable(getVariables().get(name.trim()));
    }

    public Optional<LiteralHolder> getLiteralHolder(String name) {
        int openBracket = name.indexOf('[');
        String noBracketName = name.substring(0, (openBracket == -1 ? name.length() : openBracket));

        return Optional.fromNullable(getArrayValue(name.trim(), getVariables().get(noBracketName.trim())));
    }

    private LiteralHolder getArrayValue(String name, LiteralHolder literalHolder) {
        if (literalHolder == null) {
            return null;
        }

        String bracket = Lang.instance().stringParser().getOuterBracket(name, '[', ']');
        if (bracket != null) {
            int index = Lang.instance().sequencer().parse(bracket.substring(1, bracket.length() - 1)).resolve(scriptInstance).getNumber().intValue() - 1; // Minus one cause it starts at 1
            return getArrayValue(name.replace(bracket, ""), literalHolder.getData().getArrayValue(index));
        }
        return literalHolder;
    }

    public Iterator<Variable> iterator() {
        return this.variableMap.values().iterator();
    }
}
