package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ValueContainer;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
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
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_PARSE)))
                .build());
    }

    @Override
    public Result<Double> run(Context ctx) {
        MutableValue mutableValue = ((ValueContainer) Lang.instance().sequencer().parse(ctx.getLiteral("VariableName").getString())).resolveValue(ctx);
        Literal incr = mutableValue.getDatum().get().or(0D).add(Literal.Literals.ONE); // Null values will just be 0
        mutableValue.setDatum(incr);

        return Result.<Double>builder().success().result(incr.getNumber()).build();
    }
}
