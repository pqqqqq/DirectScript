package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-10.
 */
public class ForStatement extends Statement {

    @Override
    public String getSuffix() {
        return "{";
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"for"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("VariableName").parse().build(),
                Argument.builder().name("StartValue").build(),
                Argument.builder().name("EndValue").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();

        Line endingWhile = scriptInstance.getScript().lookupEndingLine(line);
        checkNotNull(endingWhile, "Cannot find ending brace of for statement");

        int startLine = line.getScriptNumber() + 1;
        int endLine = endingWhile.getScriptNumber() - 1;

        String varName = ctx.getLiteral(0).getString();
        Variable var = scriptInstance.getEnvironment().addVariable(new Variable(varName, Literal.empty()));

        double startValue = ctx.getLiteral(1).getNumber();
        double endValue = ctx.getLiteral(2).getNumber();

        for (double x = startValue; x <= endValue; x++) {
            var.setData(Literal.getLiteralBlindly(x));
            for (int i = startLine; i <= endLine && i < scriptInstance.getScript().getLines().size(); i++) {
                Line whileLine = scriptInstance.getScript().getLines().get(i);
                scriptInstance.getResultMap().put(line, whileLine.toContex(scriptInstance).run()); // Add to result map
            }
        }

        scriptInstance.getEnvironment().getVariables().remove(varName); // Remove the variable after the loops
        scriptInstance.setSkipLines(true); // Skip lines since we've already run the code block
        return Result.success();
    }
}
