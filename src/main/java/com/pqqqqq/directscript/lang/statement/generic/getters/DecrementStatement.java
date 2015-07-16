package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.container.HolderContainer;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that decrements a variable and returns the decremented value
 */
public class DecrementStatement extends Statement<Double> {

    public DecrementStatement() {
        super(Syntax.builder()
                .prefix("--")
                .brackets()
                .arguments(Arguments.of(Argument.from("VariableName")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        LiteralHolder literalHolder = ((HolderContainer) Lang.instance().sequencer().parse(ctx.getLine(), ctx.getLiteral("VariableName").getString())).resolveHolder(ctx.getScriptInstance());
        Literal decr = literalHolder.getData().sub(Literal.Literals.ONE);
        literalHolder.setData(decr);

        return Result.<Double>builder().success().result(decr.getNumber()).build();
    }
}
