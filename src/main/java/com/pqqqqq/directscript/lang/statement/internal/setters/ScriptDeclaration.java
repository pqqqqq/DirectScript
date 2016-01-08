package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that declares a new {@link com.pqqqqq.directscript.lang.script.Script}
 */
public class ScriptDeclaration extends Statement<String> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("script")
            .suffix("{")
            .executionTime(ExecutionTime.COMPILE)
            .arguments(Arguments.of(GenericArguments.withName("ScriptName")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<String> run(Context ctx) {
        String scriptName = ctx.getLiteral("ScriptName").getString();
        return Result.<String>builder().success().result(scriptName).build();
    }
}
