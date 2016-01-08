package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kevin on 2015-12-21.
 * Formats a given date
 */
public class DateStatement extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("date")
            .arguments(Arguments.of(GenericArguments.withName("Format")), Arguments.of(GenericArguments.withName("Format"), ",", GenericArguments.withName("Date")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        String format = ctx.getLiteral("Format").getString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        long date = ctx.getLiteral("Date", System.currentTimeMillis()).getNumber().longValue();
        return Result.<String>builder().success().result(simpleDateFormat.format(new Date(date))).build();
    }
}
