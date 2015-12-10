package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-11-28.
 * A statement that iterates through a map
 */
public class ForKVStatement extends Statement {

    public ForKVStatement() {
        super(Syntax.builder()
                .identifiers("forkv")
                .suffix("{")
                .arguments(Arguments.of(Argument.from("KeyName", Argument.NO_PARSE), ",", Argument.from("ValueName", Argument.NO_PARSE), " in ", Argument.from("IterableMap")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String keyName = ctx.getLiteral("KeyName").getString();
        String valueName = ctx.getLiteral("ValueName").getString();
        Map<Datum, Datum> map = ctx.getLiteral("IterableMap").getMap();

        Block internalBlock = checkNotNull(ctx.getLine().getInternalBlock(), "This line has no internal block");
        Block.BlockRunnable blockRunnable = internalBlock.toRunnable(ctx.getScriptInstance());

        Variable key = blockRunnable.addVariable(new Variable(keyName, blockRunnable));
        Variable value = blockRunnable.addVariable(new Variable(valueName, blockRunnable));

        for (Map.Entry<Datum, Datum> entry : map.entrySet()) {
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
