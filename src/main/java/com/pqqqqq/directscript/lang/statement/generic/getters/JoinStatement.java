package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that joins an array by a glue string
 */
public class JoinStatement extends Statement<String> {

    public JoinStatement() {
        super(Syntax.builder()
                .identifiers("join", "implode")
                .arguments(Arguments.of(Argument.from("JoinArray"), ",", Argument.from("JoinString")))
                .arguments(Arguments.of(Argument.from("JoinArray"), ",", Argument.from("JoinString"), ",", Argument.from("Start")))
                .arguments(Arguments.of(Argument.from("JoinArray"), ",", Argument.from("JoinString"), ",", Argument.from("Start"), ",", Argument.from("End")))
                .build());
    }

    @Override
    public Result<String> run(Context ctx) {
        List<LiteralHolder> array = ctx.getLiteral("JoinArray").getArray();
        String joinString = ctx.getLiteral("JoinString").getString();
        int start = ctx.getLiteral("Start", 1).getNumber().intValue() - 1; // Subtract one cuz base 1
        int end = ctx.getLiteral("End", array.size()).getNumber().intValue() - 1; // Subtract one cuz base 1

        String joined = "";
        for (int i = start; i <= end && i < array.size(); i++) {
            joined += array.get(i).getData().getString() + joinString;
        }

        joined = joined.isEmpty() ? joined : joined.substring(0, joined.length() - joinString.length());
        return Result.<String>builder().success().result(joined).build();
    }
}
