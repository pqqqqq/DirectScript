package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.exception.MissingInternalBlockException;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that iterates through an array
 */
public class ForEachStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("foreach")
            .suffix("{")
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", Argument.NO_PARSE), " in ", GenericArguments.withName("IterableArray")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        String varName = ctx.getLiteral("VariableName").getString();
        List<Literal> array = ctx.getLiteral("IterableArray").getArray();

        Block internalBlock = ctx.getLine().getInternalBlock().orElseThrow(() -> new MissingInternalBlockException("For each statements must have internal blocks."));
        Block.BlockRunnable blockRunnable = internalBlock.toRunnable(ctx.getScriptInstance());
        Variable var = blockRunnable.addVariable(new Variable(varName, blockRunnable));

        for (Literal arrayVar : array) {
            var.setDatum(arrayVar);
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
