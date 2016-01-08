package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-16.
 * Represents a command specification statement
 * @see com.pqqqqq.directscript.lang.trigger.cause.Cause.CommandCause
 */
public class CommandStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("command")
            .executionTime(ExecutionTime.COMPILE)
            .arguments(Arguments.of(GenericArguments.withName("CommandAliases")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        List<String> aliases = new ArrayList<String>();
        List<Literal> array = ctx.getLiteral("CommandAliases").getArray();

        for (Literal alias : array) {
            aliases.add(alias.getString());
        }

        ctx.getScript().getCauseData().setCommandAliases(aliases.toArray(new String[aliases.size()]));
        return Result.success();
    }
}
