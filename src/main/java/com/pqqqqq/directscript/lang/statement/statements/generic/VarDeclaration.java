package com.pqqqqq.directscript.lang.statement.statements.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = { "var" })
public class VarDeclaration implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        String varName = line.getWord(1); // Var names are lenient, don't need to be a literal
        Literal value = Literal.empty();

        if (scriptInstance.getVariables().containsKey(varName)) {
            throw new IllegalArgumentException("A variable with this name already exists");
        }

        // var(0) NAME(1) =(2) VALUE(3)
        if (line.getWordCount() >= 3) {
            String EQUALS = line.getWord(2);
            checkState(EQUALS.equals("="), "Unknown word: " + EQUALS);

            value = line.getLiteral(scriptInstance, 3);
        }

        scriptInstance.getVariables().put(varName, new Variable(varName, value));
        return StatementResult.success();
    }
}
