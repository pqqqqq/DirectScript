package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 */
@Statement(identifiers = {"set"})
public class SetStatement implements IStatement {

    public StatementResult run(Line.LineContainer line) {
        String varName = line.getLiteral(0).getString();
        Literal value = line.getLiteral(1);

        Optional<Variable> variableOptional = line.getScriptInstance().getVariable(varName);
        checkState(variableOptional.isPresent(), "Unknown variable: " + varName);

        variableOptional.get().setData(value);
        return StatementResult.success();
    }
}
