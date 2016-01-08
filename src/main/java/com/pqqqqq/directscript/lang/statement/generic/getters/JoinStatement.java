package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that joins an array by a glue string
 */
public class JoinStatement extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("join", "implode")
            .arguments(Arguments.of(GenericArguments.withName("JoinArray"), ",", GenericArguments.withName("JoinString")))
            .arguments(Arguments.of(GenericArguments.withName("JoinArray"), ",", GenericArguments.withName("JoinString"), ",", GenericArguments.withName("Start")))
            .arguments(Arguments.of(GenericArguments.withName("JoinArray"), ",", GenericArguments.withName("JoinString"), ",", GenericArguments.withName("Start"), ",", GenericArguments.withName("End")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        List<Literal> array = ctx.getLiteral("JoinArray").getArray();
        String joinString = ctx.getLiteral("JoinString").getString();
        int start = ctx.getLiteral("Start", 1).getNumber().intValue() - 1; // Subtract one cuz base 1
        int end = ctx.getLiteral("End", array.size()).getNumber().intValue() - 1; // Subtract one cuz base 1

        String joined = "";
        for (int i = start; i <= end && i < array.size(); i++) {
            joined += array.get(i).getString() + joinString;
        }

        joined = joined.isEmpty() ? joined : joined.substring(0, joined.length() - joinString.length());
        return Result.<String>builder().success().result(joined).build();
    }
}
