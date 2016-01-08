package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Calls a certain {@link Script} with given arguments
 */
public class CallStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("call", "run")
            .arguments(Arguments.of(GenericArguments.withName("ScriptName")), Arguments.of(GenericArguments.withName("ScriptName"), ",", GenericArguments.withName("ArgumentArray")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        String scriptName = ctx.getLiteral("ScriptName").getString();
        Optional<Script> scriptOptional = Lang.instance().getScript(scriptName);

        checkState(scriptOptional.isPresent(), "Unknown script: " + scriptName);

        Script script = scriptOptional.get();
        checkState(script.getTrigger().get().hasCause(Causes.CALL), "This script cannot be called");

        List<Literal> arguments = ctx.getLiteral("ArgumentArray", Literal.Literals.EMPTY_ARRAY).getArray();

        ScriptInstance scriptInstanceNew = ScriptInstance.builder().script(script).cause(Causes.CALL).eventVar(ctx.getScriptInstance().getEventVars()).eventVar("Arguments", arguments).build();
        scriptInstanceNew.execute();

        Literal returnLiteral = scriptInstanceNew.getReturnValue().orElse(null);
        Object returnValue = (returnLiteral == null ? null : returnLiteral.getValue().orElse(null));
        return Result.builder().success().result(returnValue).build();
    }
}
