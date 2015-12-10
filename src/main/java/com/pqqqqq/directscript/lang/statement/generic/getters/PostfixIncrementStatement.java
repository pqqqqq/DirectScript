package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ValueContainer;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
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
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_PARSE)))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        MutableValue mutableValue = ((ValueContainer) Lang.instance().sequencer().parse(ctx.getLiteral("VariableName").getString())).resolveValue(ctx);
        Literal before = mutableValue.getDatum().get().or(0D); // Null values will just be 0
        mutableValue.setDatum(before.add(Literal.Literals.ONE));

        return Result.<Double>builder().success().result(before.getNumber()).build();
    }
}
