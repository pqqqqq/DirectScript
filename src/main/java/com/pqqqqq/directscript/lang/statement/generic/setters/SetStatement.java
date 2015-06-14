package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 * A statement that sets the value of an existing {@link Variable}
 */
public class SetStatement extends Statement {

    @Override
    public String getSplitString() {
        return " ";
    }

    @Override
    public boolean doesUseBrackets() {
        return false;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"set"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("VariableName").parse().build(),
                Argument.builder().name("=").parse().modifier().build(),
                Argument.builder().name("Value").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        String varName = ctx.getLiteral(0).getString();
        Literal value = ctx.getLiteral(2);

        Optional<Variable> variableOptional = ctx.getScriptInstance().getEnvironment().getVariable(varName);
        checkState(variableOptional.isPresent(), "Unknown variable: " + varName);

        variableOptional.get().setData(value);
        return Result.success();
    }
}
