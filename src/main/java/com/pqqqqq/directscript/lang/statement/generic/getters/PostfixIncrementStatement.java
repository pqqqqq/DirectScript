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
 * A statement that increments a variable and returns the value before the increment
 */
public class PostfixIncrementStatement extends Statement<Double> {

    public PostfixIncrementStatement() {
        super(Syntax.builder()
                .suffix("++")
                .brackets()
                .arguments(Arguments.of(Argument.from("VariableName")))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        LiteralHolder literalHolder = ((HolderContainer) Lang.instance().sequencer().parse(ctx.getLine(), ctx.getLiteral("VariableName").getString())).resolveHolder(ctx.getScriptInstance());
        Literal before = literalHolder.getData();
        literalHolder.setData(before.add(Literals.ONE));

        return Result.<Double>builder().success().result(before.getNumber()).literal(before).build();
    }
}
