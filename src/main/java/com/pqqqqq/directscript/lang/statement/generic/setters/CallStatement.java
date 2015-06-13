package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
public class CallStatement extends Statement {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"call", "run"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("ScriptName").build(),
                Argument.builder().name("ArgumentArray").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        String scriptName = ctx.getLiteral(0).getString();
        Optional<Script> scriptOptional = DirectScript.instance().getScript(scriptName);

        checkState(scriptOptional.isPresent(), "Unknown script: " + scriptName);

        Script script = scriptOptional.get();
        checkState(script.getTrigger().get().hasCause(Causes.CALL), "This script cannot be called");

        List<Variable> arguments = ctx.getLiteral(1).getArray();

        ScriptInstance scriptInstanceNew = ScriptInstance.builder().script(script).cause(Causes.CALL).causedBy(ctx.getScriptInstance().getCausedBy().orNull()).build();
        scriptInstanceNew.getEnvironment().getVariables().put("generic.arguments", new Variable("generic.arguments", Literal.getLiteralBlindly(arguments)));
        scriptInstanceNew.run();

        Literal returnLiteral = scriptInstanceNew.getReturnValue().orNull();
        Object returnValue = (returnLiteral == null ? null : returnLiteral.getValue().orNull());
        return Result.builder().success().result(returnValue).literal(returnValue).build();
    }
}