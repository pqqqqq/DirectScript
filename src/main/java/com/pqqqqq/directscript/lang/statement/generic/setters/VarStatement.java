package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Literals;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that creates a new {@link Variable} in the {@link Environment}
 */
public class VarStatement extends Statement {

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
        return new String[]{"var"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("final").optional().parse().modifier().build(),
                Argument.builder().name("VariableName").parse().build(),
                Argument.builder().name("=").optional().parse().modifier().build(),
                Argument.builder().name("Value").optional().rest().build()
        };
    }

    @Override
    public Result run(Context ctx) {
        boolean isFinal = false;
        Literal value = Literals.EMPTY;

        for (int i = 0; i < ctx.getLiteralCount(); i++) {
            String word = ctx.getLiteral(i).getString();

            // Check modifiers first (eg final)
            if (word.equals("final")) {
                isFinal = true;
            } else {
                if (ctx.getLiteralCount() > (i + 2)) {
                    value = ctx.getLiteral(i + 2);
                }

                ctx.getScriptInstance().addVariable(new Variable(word, value, isFinal));
                return Result.success();
            }
        }

        throw new IllegalArgumentException("Improper variable declaration: " + ctx.getLine());
    }
}
