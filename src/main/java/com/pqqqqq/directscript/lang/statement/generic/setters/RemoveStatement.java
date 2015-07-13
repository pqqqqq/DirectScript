package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2015-07-11.
 * A statement that removes a KV pair from a map
 */
public class RemoveStatement extends Statement {

    public RemoveStatement() {
        super(Syntax.builder()
                .identifiers("remove")
                .arguments(Arguments.of(Argument.from("Map"), ",", Argument.from("Key")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        Literal key = ctx.getLiteral("Key");

        Map<LiteralHolder, LiteralHolder> map = ctx.getLiteral("Map").getMap();
        Map<LiteralHolder, LiteralHolder> clone = new HashMap<LiteralHolder, LiteralHolder>();
        clone.putAll(map);

        for (Map.Entry<LiteralHolder, LiteralHolder> entry : clone.entrySet()) {
            if (entry.getKey().getData().equals(key)) {
                map.remove(entry.getKey());
                return Result.success();
            }
        }

        return Result.failure();
    }
}
