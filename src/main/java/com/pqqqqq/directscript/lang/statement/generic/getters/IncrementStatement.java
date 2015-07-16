package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.container.HolderContainer;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that increments a variable and returns the incremented value
 */
public class IncrementStatement extends Statement<Double> {

    public IncrementStatement() {
        super(Syntax.builder()
                .prefix("++")
                .brackets()
                .arguments(Arguments.of(Argument.from("VariableName")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        LiteralHolder literalHolder = ((HolderContainer) Lang.instance().sequencer().parse(ctx.getLine(), ctx.getLiteral("VariableName").getString())).resolveHolder(ctx.getScriptInstance());
        Literal incr = literalHolder.getData().add(Literal.Literals.ONE);
        literalHolder.setData(incr);

        return Result.<Double>builder().success().result(incr.getNumber()).build();
    }
}
