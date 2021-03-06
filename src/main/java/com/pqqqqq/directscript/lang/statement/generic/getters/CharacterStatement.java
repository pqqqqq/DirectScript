package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-16.
 * A statement that gets the character by its ASCII ordinal
 */
public class CharacterStatement extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("character", "chr")
            .arguments(Arguments.of(GenericArguments.withName("Ordinal")))
            .build();


    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        int ordinal = ctx.getLiteral("Ordinal").getNumber().intValue();
        char character = (char) ordinal;
        String result = Character.toString(character);

        return Result.<String>builder().success().result(result).build();
    }
}
