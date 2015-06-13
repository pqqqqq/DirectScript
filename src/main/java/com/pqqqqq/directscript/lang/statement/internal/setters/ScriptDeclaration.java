package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-02.
 */
public class ScriptDeclaration extends Statement<String> {

    @Override
    public ExecutionTime getExecutionTime() {
        return ExecutionTime.COMPILE;
    }

    @Override
    public String getSuffix() {
        return "{";
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"script"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("ScriptName").build()
        };
    }

    @Override
    public Result<String> run(Context ctx) {
        String scriptName = ctx.getLiteral(0).getString();
        return Result.<String>builder().success().result(scriptName).build();
    }
}
