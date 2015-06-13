package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-10.
 */
public class ForEachStatement extends Statement {

    @Override
    public String getSuffix() {
        return "{";
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{"foreach"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("VariableName").parse().build(),
                Argument.builder().name("IterableArray").build()
        };
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();

        Line endingWhile = scriptInstance.getScript().lookupEndingLine(line);
        checkNotNull(endingWhile, "Cannot find ending brace of foreach statement");

        int startLine = line.getScriptNumber() + 1;
        int endLine = endingWhile.getScriptNumber() - 1;

        String varName = ctx.getLiteral(0).getString();
        Variable var = scriptInstance.getEnvironment().addVariable(new Variable(varName, Literal.empty()));
        List<Variable> array = ctx.getLiteral(1).getArray();

        for (Variable arrayVar : array) {
            var.setData(arrayVar.getData());
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
