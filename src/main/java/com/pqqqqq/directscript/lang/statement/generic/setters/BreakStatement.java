package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-14.
 * Represents a break statement which ceases an iteration/loop
 */
public class BreakStatement extends Statement {

    public BreakStatement() {
        super(Syntax.builder()
                .identifiers("break")
                .brackets()
                .build());
    }

    @Override
    public Result run(Context ctx) {
        return Result.success();
    }
}
