package com.pqqqqq.directscript.lang.statement.internal.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-05.
 * A statement that terminates any blocked code
 */
public class Termination extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("}")
            .brackets()
            .executionTime(ExecutionTime.ALWAYS)
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        return Result.success();
    }
}
