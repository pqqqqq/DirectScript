package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.Literals;
import com.pqqqqq.directscript.lang.data.container.HolderContainer;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that decrements a variable and returns the decremented value
 */
public class DecrementStatement extends Statement<Double> {

    @Override
    public String getPrefix() {
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
        LiteralHolder literalHolder = ((HolderContainer) Lang.instance().sequencer().parse(ctx.getLiteral(0).getString())).resolveHolder(ctx.getScriptInstance());
        Literal decr = literalHolder.getData().sub(Literals.ONE);
        literalHolder.setData(decr);

        return Result.<Double>builder().success().result(decr.getNumber()).literal(decr).build();
    }
}