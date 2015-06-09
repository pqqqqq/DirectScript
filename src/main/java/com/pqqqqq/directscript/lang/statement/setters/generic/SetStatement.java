package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.util.StringParser;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 */
@Statement(identifiers = {"set"})
public class SetStatement implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        String[] words = StringParser.instance().parseSplit(line.getTrimmedLine(), " "); // Split spaces
        String varName = words[0];
        String EQUALS = words[1];
        String value = StringUtils.join(words, " ", 2, words.length);

        checkState(EQUALS.equals("="), "Expected '=', got: " + EQUALS);

        Optional<Variable> variableOptional = scriptInstance.getVariable(varName);
        checkState(variableOptional.isPresent(), "Unknown variable: " + varName);

        Variable variable = variableOptional.get();
        checkState(!variable.isFinal(), "Cannot change a final variable");

        variable.setData(scriptInstance.getSequencer().parse(value));
        return StatementResult.success();
    }
}
