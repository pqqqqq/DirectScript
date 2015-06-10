package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = { "call", "run" })
public class CallStatement implements IStatement {

    public StatementResult run(Line.LineContainer line) {
        String scriptName = line.getLiteral(0).getString();
        Optional<Script> scriptOptional = DirectScript.instance().getScript(scriptName);

        checkState(scriptOptional.isPresent(), "Unknown script: " + scriptName);

        Script script = scriptOptional.get();

        checkState(script.getTrigger().get().hasCause(Causes.CALL), "This script cannot be called");

        ScriptInstance scriptInstanceNew = ScriptInstance.builder().script(script).cause(Causes.CALL).build();

        for (int i = 1; i < line.getLiteralCount(); i += 2) {
            String varName = line.getLiteral(i).getString();
            Literal value = line.getLiteral(i + 1);

            if (!scriptInstanceNew.getVariables().containsKey(varName)) {
                scriptInstanceNew.getVariables().put(varName, new Variable(varName, value));
            }
        }

        scriptInstanceNew.run();
        return StatementResult.success();
    }

    public boolean apply(Line input) {
        return false;
    }
}
