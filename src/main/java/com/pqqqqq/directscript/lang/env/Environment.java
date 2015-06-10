package com.pqqqqq.directscript.lang.env;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.data.variable.IVariableContainer;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.util.StringParser;
import com.pqqqqq.directscript.lang.util.Utilities;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 2015-06-02.
 * Represents an environment that branches from a {@link Script}
 */
public abstract class Environment implements IVariableContainer {
    @Nonnull
    private final Map<String, Variable> variableMap;

    protected Environment(Map<String, Variable> variableMap) {
        this.variableMap = variableMap;
    }

    public abstract Script getScript();

    public abstract Cause getCause();

    public Map<String, Variable> getVariables() {
        return this.variableMap;
    }

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
            int index = Integer.parseInt(bracket.substring(1, bracket.length() - 1));
            List<Variable> variableList = variable.getData().getArray();

            Utilities.buildToIndex(variableList, index, Variable.empty());
            return getArrayValue(name.replace(bracket, ""), variableList.get(index));
        }
        return variable;
    }
}
