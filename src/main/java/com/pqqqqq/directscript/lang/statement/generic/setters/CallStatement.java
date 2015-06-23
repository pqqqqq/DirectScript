package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Calls a certain {@link Script} with given arguments
 */
public class CallStatement extends Statement {

    public CallStatement() {
        super(Syntax.builder()
                .identifiers("call", "run")
                .arguments(Arguments.of(Argument.from("ScriptName"), ",", Argument.from("ArgumentArray")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String scriptName = ctx.getLiteral("ScriptName").getString();
        Optional<Script> scriptOptional = Lang.instance().getScript(scriptName);

        checkState(scriptOptional.isPresent(), "Unknown script: " + scriptName);

        Script script = scriptOptional.get();
        checkState(script.getTrigger().get().hasCause(Causes.CALL), "This script cannot be called");

        List<LiteralHolder> arguments = ctx.getLiteral("ArgumentArray").getArray();

        ScriptInstance scriptInstanceNew = ScriptInstance.builder().script(script).cause(Causes.CALL).causedBy(ctx.getScriptInstance().getCausedBy().orNull()).build();
        scriptInstanceNew.addVariable(new Variable("generic.arguments", Literal.getLiteralBlindly(arguments)));
        scriptInstanceNew.execute();

        Literal returnLiteral = scriptInstanceNew.getReturnValue().orNull();
        Object returnValue = (returnLiteral == null ? null : returnLiteral.getValue().orNull());
        return Result.builder().success().result(returnValue).literal(returnValue).build();
    }
}
