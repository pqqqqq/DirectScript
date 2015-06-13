package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

/**
 * Created by Kevin on 2015-06-09.
 */
public class JoinStatement extends Statement<String> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"join"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("JoinArray").build(),
                Argument.builder().name("JoinString").build(),
                Argument.builder().name("Start").optional().build(),
                Argument.builder().name("End").optional().build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        List<Variable> array = ctx.getLiteral(0).getArray();
        String joinString = ctx.getLiteral(1).getString();
        int start = ctx.getLiteral(2, 0).getNumber().intValue();
        int end = ctx.getLiteral(3, array.size()).getNumber().intValue();

        String joined = "";
        for (int i = start; i < end && i < array.size(); i++) {
            joined += array.get(i).getData().getString() + joinString;
        }

        joined = joined.isEmpty() ? joined : joined.substring(0, joined.length() - joinString.length());
        return Result.<String>builder().success().result(joined).literal(joined).build();
    }
}
