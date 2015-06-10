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

import java.util.ArrayList;
import java.util.List;

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

        List<Variable> arguments = new ArrayList<Variable>();
        for (int i = 1; i < line.getLiteralCount(); i++) {
            arguments.add(new Variable(null, line.getLiteral(i)));
        }

        ScriptInstance scriptInstanceNew = ScriptInstance.builder().script(script).cause(Causes.CALL).causedBy(line.getScriptInstance().getCausedBy().orNull()).build();
        scriptInstanceNew.getVariables().put("generic.arguments", new Variable("generic.arguments", Literal.getLiteralBlindly(arguments)));
        scriptInstanceNew.run();

        Literal returnLiteral = scriptInstanceNew.getReturnValue().orNull();
        Object returnValue = (returnLiteral == null ? null : returnLiteral.getValue().orNull());
        return StatementResult.builder().success().result(returnValue).literal(returnValue).build();
    }
}
