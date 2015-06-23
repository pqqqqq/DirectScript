package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-14.
 * Represents a continue statement which moves to the next iteration in an loop
 */
public class ContinueStatement extends Statement {

    public ContinueStatement() {
        super(Syntax.builder()
                .identifiers("continue")
                .brackets()
                .build());
    }

    @Override
    public Result run(Context ctx) {
        return Result.success();
    }
}
