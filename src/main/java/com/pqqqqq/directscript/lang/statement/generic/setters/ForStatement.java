package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.exception.MissingInternalBlockException;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that counts a variable up
 */
public class ForStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("for")
            .suffix("{")
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", Argument.NO_PARSE), "=", GenericArguments.withName("StartValue"), ",", GenericArguments.withName("EndValue")))
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", Argument.NO_PARSE), "=", GenericArguments.withName("StartValue"), ",", GenericArguments.withName("EndValue"), ",", GenericArguments.withName("IncrementValue")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        String varName = ctx.getLiteral("VariableName").getString();

        double startValue = ctx.getLiteral("StartValue").getNumber();
        double endValue = ctx.getLiteral("EndValue").getNumber();
        double increment = ctx.getLiteral("IncrementValue", 1).getNumber();

        Block internalBlock = ctx.getLine().getInternalBlock().orElseThrow(() -> new MissingInternalBlockException("For statements must have internal blocks."));
        Block.BlockRunnable blockRunnable = internalBlock.toRunnable(ctx.getScriptInstance());
        Variable var = blockRunnable.addVariable(new Variable(varName, blockRunnable));

        for (double x = startValue; (startValue <= endValue ? x <= endValue : x >= endValue); x += increment) {
            var.setDatum(Literal.fromObject(x));
            ScriptInstance.Result result = blockRunnable.execute();

            if (result == ScriptInstance.Result.FAILURE_BREAK) {
                break;
            }

            if (result == ScriptInstance.Result.FAILURE_CONTINUE) {
                continue;
            }
        }

        return Result.success();
    }
}
