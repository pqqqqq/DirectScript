package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.container.DataContainer;
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

    public WhileStatement() {
        super(Syntax.builder()
                .identifiers("while")
                .suffix("{")
                .arguments(Arguments.of(Argument.from("Condition", Argument.NO_RESOLVE)))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Block internalBlock = checkNotNull(ctx.getLine().getInternalBlock(), "This line has no internal block");
        DataContainer container = ctx.getContainer("Condition");

        while (container.resolve(ctx.getScriptInstance()).getBoolean()) { // This needs to be re-parsed every time
            ScriptInstance.Result result = ctx.getScriptInstance().execute(internalBlock);

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
