package com.pqqqqq.directscript.lang.statement.statements.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = { "var" })
public class VarDeclaration implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        String[] words = StringUtil.splitNotInQuotes(line.getArg(0), " "); // Split spaces
        String varName = words[0]; // Var names are lenient, don't need to be a literal
        Literal value = Literal.empty();

        if (!Variable.namePattern().matcher(varName).matches()) {
            throw new IllegalArgumentException("This variable name has illegal characters (only alphanumeric)");
        }

        if (scriptInstance.getVariables().containsKey(varName)) {
            throw new IllegalArgumentException("A variable with this name already exists");
        }

        // var(0) NAME(1) =(2) VALUE(3)
        if (words.length >= 3) {
            String EQUALS = words[1];
            checkState(EQUALS.equals("="), "Unknown word: " + EQUALS);

            value = scriptInstance.getSequencer().parse(StringUtils.join(words, " ", 2, words.length));
        }

        scriptInstance.getVariables().put(varName, new Variable(varName, value));
        return StatementResult.success();
    }
}
