package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-16.
 * Represents a command specification statement
 */
public class CommandStatement extends Statement {

    @Override
    public ExecutionTime getExecutionTime() {
        return ExecutionTime.COMPILE;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"command"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("CommandAliases").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        List<String> aliases = new ArrayList<String>();
        List<Variable> array = ctx.getLiteral(0).getArray();

        for (Variable alias : array) {
            aliases.add(alias.getData().getString());
        }

        ctx.getScript().getCauseData().setCommandAliases(aliases.toArray(new String[aliases.size()]));
        return Result.success();
    }
}
