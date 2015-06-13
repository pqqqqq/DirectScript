package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-09.
 * A statement that consecutively executes its block code until its condition is false
 */
public class WhileStatement extends Statement {

    @Override
    public String getSuffix() {
        return "{";
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"while"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Condition").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();

        Line endingWhile = scriptInstance.getScript().lookupEndingLine(line);
        checkNotNull(endingWhile, "Cannot find ending brace of while statement");

        int startLine = line.getScriptNumber() + 1;
        int endLine = endingWhile.getScriptNumber() - 1;

        while (ctx.getLiteral(0).getBoolean()) {
            for (int i = startLine; i <= endLine && i < scriptInstance.getScript().getLines().size(); i++) {
                Line whileLine = scriptInstance.getScript().getLines().get(i);
                scriptInstance.getResultMap().put(line, whileLine.toContex(scriptInstance).run()); // Add to result map
            }
        }

        scriptInstance.setSkipLines(true); // Skip lines since we've already run the code block
        return Result.success();
    }
}
