package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-14.
 * Represents a continue statement which moves to the next iteration in an loop
 */
public class ContinueStatement extends Statement {

    @Override
    public String getSplitString() {
        return " ";
    }

    @Override
    public boolean doesUseBrackets() {
        return false;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"continue"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[0];
    }

    @Override
    public Result run(Context ctx) {
        return Result.success();
    }
}
