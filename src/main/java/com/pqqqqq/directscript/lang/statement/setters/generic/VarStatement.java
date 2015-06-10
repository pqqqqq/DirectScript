package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = {"var"})
public class VarStatement implements IStatement {

    public StatementResult run(Line.LineContainer line) {
        boolean isFinal = false;
        Literal value = Literal.empty();

        for (int i = 0; i < line.getLiteralCount(); i++) {
            String word = line.getLiteral(i).getString();

            // Check modifiers first (eg final)
            if (word.equals("final")) {
                isFinal = true;
            } else {
                // Done with modifiers, start with name and value
                checkState(Variable.namePattern().matcher(word).matches(), "This variable name has illegal characters (only alphanumeric/period and must start with alphabetic).");
                checkState(!line.getScriptInstance().getVariables().containsKey(word), "A variable with this name already exists");

                if (line.getLiteralCount() > i) {
                    value = line.getLiteral(i + 1);
                }

                line.getScriptInstance().getVariables().put(word, new Variable(word, value, isFinal));
                return StatementResult.success();
            }
        }

        throw new IllegalArgumentException("Improper variable declaration: " + line.getLine());
    }
}
