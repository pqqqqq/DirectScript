package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.flowpowered.math.GenericMath;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Argument;
import com.pqqqqq.directscript.lang.statement.Result;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-10.
 */
public class RoundStatement extends Statement<Double> {

    @Override
    public String[] getIdentifiers() {
        return new String[]{"round"};
    }

    @Override
    public Argument[] getArguments() {
        return new Argument[]{
                Argument.builder().name("Number").build()
        };
    }

    @Override
    public Result<Double> run(Context ctx) {
        double rounded = GenericMath.round(ctx.getLiteral(0).getNumber(), 0);
        return Result.<Double>builder().success().result(rounded).literal(rounded).build();
    }
}
