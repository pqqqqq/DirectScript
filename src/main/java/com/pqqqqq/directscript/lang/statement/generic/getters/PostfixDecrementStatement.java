package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.Literals;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that decrements a variable and returns the value before the decrement
 */
public class PostfixDecrementStatement extends Statement<Double> {

    @Override
    public String getSuffix() {
        return "--";
    }

    @Override
    public String getSplitString() {
        return "";
    }

    @Override
    public boolean doesUseBrackets() {
        return false;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[0];
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("VariableName").parse().build()
        };
    }

    @Override
    public Result<Double> run(Context ctx) {
        String varName = ctx.getLiteral(0).getString();

        Optional<LiteralHolder> literalHolderOptional = ctx.getScriptInstance().getEnvironment().getLiteralHolder(varName);
        checkState(literalHolderOptional.isPresent(), "Unknown variable: " + varName);

        Literal before = literalHolderOptional.get().getData();
        literalHolderOptional.get().setData(before.sub(Literals.ONE));
        return Result.<Double>builder().success().result(before.getNumber()).literal(before).build();
    }
}
