package com.pqqqqq.directscript.lang.data.variable;

import com.google.common.base.Optional;

import java.util.Map;

/**
 * Created by Kevin on 2015-06-02.
 */
public interface IVariableContainer {

    Variable addVariable(Variable variable);

    Map<String, Variable> getVariables();

    Optional<Variable> getVariable(String name);
}
