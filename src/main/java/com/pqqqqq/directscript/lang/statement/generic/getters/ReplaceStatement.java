package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that replaces all entries in a literal
 */
public class ReplaceStatement extends Statement<String> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"replace"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("String").build(),
                Argument.builder().name("FindString").build(),
                Argument.builder().name("Replace").build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        String string = ctx.getLiteral(0).getString();
        String find = ctx.getLiteral(1).getString();
        String replace = ctx.getLiteral(2).getString();
        String result = string.replace(find, replace);

        return Result.<String>builder().success().result(result).literal(result).build();
    }
}
