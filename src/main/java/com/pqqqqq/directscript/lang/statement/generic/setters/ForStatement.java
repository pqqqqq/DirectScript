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
                .arguments(Arguments.of(Argument.builder().name("VariableName").parse().build(), "=", Argument.from("StartValue"), ",", Argument.from("EndValue")))
                .arguments(Arguments.of(Argument.builder().name("VariableName").parse().build(), "=", Argument.from("StartValue"), ",", Argument.from("EndValue"), ",", Argument.from("IncrementValue")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String varName = ctx.getLiteral("VariableName").getString();
        Variable var = ctx.getScriptInstance().addVariable(new Variable(varName));

        double startValue = ctx.getLiteral("StartValue").getNumber();
        double endValue = ctx.getLiteral("EndValue").getNumber();
        double increment = ctx.getLiteral("IncrementValue", 1).getNumber();

        Block internalBlock = ctx.getLine().getInternalBlock();
        checkNotNull(internalBlock, "This line has no internal block");

        for (double x = startValue; (startValue <= endValue ? x <= endValue : x >= endValue); x += increment) {
            var.setData(Literal.getLiteralBlindly(x));
            ScriptInstance.Result result = ctx.getScriptInstance().execute(internalBlock);

            if (result == ScriptInstance.Result.FAILURE_BREAK) {
                break;
            }

            if (result == ScriptInstance.Result.FAILURE_CONTINUE) {
                continue;
            }
        }

        ctx.getScriptInstance().getVariables().remove(varName); // Remove the variable after the loops
        ctx.getScriptInstance().setSkipToLine(ctx.getLine().getClosingBrace()); // Skip lines since we've already run the code block
        return Result.success();
    }
}
