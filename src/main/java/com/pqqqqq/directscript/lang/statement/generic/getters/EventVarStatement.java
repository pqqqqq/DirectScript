package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-27.
 * A statement that gets a literal value from an event
 */
public class EventVarStatement extends Statement<Object> {

    public EventVarStatement() {
        super(Syntax.builder()
                .identifiers("eventvar")
                .arguments(Arguments.of(Argument.from("String")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        String eventVar = ctx.getLiteral("String").getString();
        Object value = checkNotNull(ctx.getScriptInstance().getEventVars().get(eventVar), "There is no event var with this key");
        return Result.builder().success().result(value).build();
    }
}
