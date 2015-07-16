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
        List<LiteralHolder> array = ctx.getLiteral("IterableArray").getArray();

        Block internalBlock = checkNotNull(ctx.getLine().getInternalBlock(), "This line has no internal block");
        Block.BlockRunnable blockRunnable = internalBlock.toRunnable(ctx.getScriptInstance());
        Variable var = blockRunnable.addVariable(new Variable(varName));

        for (LiteralHolder arrayVar : array) {
            var.setData(arrayVar.getData());
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
