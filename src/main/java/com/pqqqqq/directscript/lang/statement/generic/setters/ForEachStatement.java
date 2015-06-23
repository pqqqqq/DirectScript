package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that iterates through an array
 */
public class ForEachStatement extends Statement {

    public ForEachStatement() {
        super(Syntax.builder()
                .identifiers("foreach")
                .suffix("{")
                .arguments(Arguments.of(Argument.builder().name("VariableName").parse().build(), " in ", Argument.from("IterableArray")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String varName = ctx.getLiteral("VariableName").getString();
        Variable var = ctx.getScriptInstance().addVariable(new Variable(varName));
        List<LiteralHolder> array = ctx.getLiteral("IterableArray").getArray();

        Block internalBlock = ctx.getLine().getInternalBlock();
        checkNotNull(internalBlock, "This line has no internal block");

        for (LiteralHolder arrayVar : array) {
            var.setData(arrayVar.getData());
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
