package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

/**
 * Created by Kevin on 2015-12-21.
 * Creates a sublist of data
 */
public class SublistStatement extends Statement<List<Literal>> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("sublist", "subList", "subarray", "subArray")
            .arguments(Arguments.of(GenericArguments.withName("Array"), ",", GenericArguments.withName("From")))
            .arguments(Arguments.of(GenericArguments.withName("Array"), ",", GenericArguments.withName("From"), ",", GenericArguments.withName("To")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<List<Literal>> run(Context ctx) {
        List<Literal> array = ctx.getLiteral("Array").getArray();
        int from = ctx.getLiteral("From").getNumber().intValue() - 1; // Base 1
        int to = ctx.getLiteral("To", array.size() + 1).getNumber().intValue() - 1; // Base 1

        return Result.<List<Literal>>builder().success().result(array.subList(from, to)).build();
    }
}
