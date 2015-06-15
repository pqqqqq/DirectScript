package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-10.
 * A statement that iterates through an array
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
        String varName = ctx.getLiteral(0).getString();
        Variable var = ctx.getScriptInstance().getEnvironment().addVariable(new Variable(varName, Literal.empty()));
        List<Variable> array = ctx.getLiteral(1).getArray();

        Block internalBlock = ctx.getLine().getInternalBlock();
        checkNotNull(internalBlock, "This line has no internal block");

        for (Variable arrayVar : array) {
            var.setData(arrayVar.getData());
            ScriptInstance.Result result = ctx.getScriptInstance().execute(internalBlock);

            if (result == ScriptInstance.Result.FAILURE_BREAK) {
                break;
            }

            if (result == ScriptInstance.Result.FAILURE_CONTINUE) {
                continue;
            }
        }

        ctx.getScriptInstance().getEnvironment().getVariables().remove(varName); // Remove the variable after the loops
        ctx.getScriptInstance().setSkipToLine(ctx.getLine().getLinkedLine()); // Skip lines since we've already run the code block
        return Result.success();
    }
}
