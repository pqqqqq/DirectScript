package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-04.
 */
@Statement(identifiers = {"if"}, suffix = "{")
public class IfStatement implements IStatement<Boolean> {

    public StatementResult<Boolean> run(ScriptInstance scriptInstance, Line line) {
        String conditionString = line.getArg(0);
        conditionString = conditionString.substring(conditionString.indexOf('(') + 1, conditionString.lastIndexOf(')')); // Take everything in the brackets

        Literal conditionLiteral = scriptInstance.getSequencer().parse(conditionString);
        if (!conditionLiteral.isBoolean()) {
            throw new IllegalArgumentException("Invalid condition: " + conditionString);
        }

        boolean result = conditionLiteral.getBoolean();
        if (!result) {
            scriptInstance.setSkipLines(true);
        }

        return StatementResult.<Boolean>builder().success().result(result).build();
    }
}
