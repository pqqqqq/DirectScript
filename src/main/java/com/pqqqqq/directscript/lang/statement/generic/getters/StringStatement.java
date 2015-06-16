package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-15.
 * A statement that returns the string value of a literal
 */
public class StringStatement extends Statement<String> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"string"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Literal").build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        String stringValue = ctx.getLiteral(0).getString();
        return Result.<String>builder().success().result(stringValue).literal(stringValue).build();
    }
}
