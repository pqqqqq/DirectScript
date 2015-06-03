package com.pqqqqq.directscript.lang.statement.statements.generic;

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

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = { "call", "run" })
public class CallStatement implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        String scriptName = line.getLiteral(scriptInstance, 1).getString();
        Optional<Script> script = DirectScript.instance().getScript(scriptName);

        if (!script.isPresent()) {
            throw new IllegalArgumentException("Unknown script: " + scriptName);
        }

        ScriptInstance scriptInstanceNew = ScriptInstance.builder().script(script.get()).cause(Causes.CALL).build();
        for (int i = 2; i < line.getWordCount(); i += 2) {
            String varName = line.getLiteral(scriptInstance, i).getString();
            Literal value = line.getLiteral(scriptInstance, i + 1);

            if (!scriptInstanceNew.getVariables().containsKey(varName)) {
                scriptInstanceNew.getVariables().put(varName, new Variable(varName, value));
            }
        }

        script.get().run(scriptInstanceNew);
        return StatementResult.success();
    }
}
