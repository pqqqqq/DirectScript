package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.exception.MissingInternalBlockException;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Map;

/**
 * Created by Kevin on 2015-11-28.
 * A statement that iterates through a map
 */
public class ForKVStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("forkv")
            .suffix("{")
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("KeyName", Argument.NO_PARSE), ",", GenericArguments.withNameAndFlags("ValueName", Argument.NO_PARSE), " in ", GenericArguments.withName("IterableMap")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        String keyName = ctx.getLiteral("KeyName").getString();
        String valueName = ctx.getLiteral("ValueName").getString();
        Map<Literal, Literal> map = ctx.getLiteral("IterableMap").getMap();

        Block internalBlock = ctx.getLine().getInternalBlock().orElseThrow(() -> new MissingInternalBlockException("ForKV statements must have internal blocks."));
        Block.BlockRunnable blockRunnable = internalBlock.toRunnable(ctx.getScriptInstance());

        Variable key = blockRunnable.addVariable(new Variable(keyName, blockRunnable));
        Variable value = blockRunnable.addVariable(new Variable(valueName, blockRunnable));

        for (Map.Entry<Literal, Literal> entry : map.entrySet()) {
            key.setDatum(entry.getKey());
            value.setDatum(entry.getValue());

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
