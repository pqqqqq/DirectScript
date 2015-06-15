package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that consecutively executes its block code until its condition is false
 */
public class WhileStatement extends Statement {

    @Override
    public String getSuffix() {
        return "{";
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"while"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Condition").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        Block internalBlock = ctx.getLine().getInternalBlock();
        checkNotNull(internalBlock, "This line has no internal block");

        while (ctx.getLiteral(0).getBoolean()) {
            ScriptInstance.Result result = ctx.getScriptInstance().run(internalBlock);

            if (result == ScriptInstance.Result.FAILURE_BREAK) {
                break;
            }

            if (result == ScriptInstance.Result.FAILURE_CONTINUE) {
                continue;
            }
        }

        ctx.getScriptInstance().setSkipToLine(ctx.getLine().getLinkedLine()); // Skip lines since we've already run the code block
        return Result.success();
    }
}
