package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that counts a variable up
 */
public class ForStatement extends Statement {

    public ForStatement() {
        super(Syntax.builder()
                .identifiers("for")
                .suffix("{")
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_PARSE), "=", Argument.from("StartValue"), ",", Argument.from("EndValue")))
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_PARSE), "=", Argument.from("StartValue"), ",", Argument.from("EndValue"), ",", Argument.from("IncrementValue")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String varName = ctx.getLiteral("VariableName").getString();

        double startValue = ctx.getLiteral("StartValue").getNumber();
        double endValue = ctx.getLiteral("EndValue").getNumber();
        double increment = ctx.getLiteral("IncrementValue", 1).getNumber();

        Block internalBlock = checkNotNull(ctx.getLine().getInternalBlock(), "This line has no internal block");
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
