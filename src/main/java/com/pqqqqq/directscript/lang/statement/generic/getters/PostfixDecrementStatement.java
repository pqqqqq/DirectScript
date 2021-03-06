package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ValueContainer;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-18.
 * A statement that decrements a variable and returns the value before the decrement
 */
public class PostfixDecrementStatement extends Statement<Double> {
    public static final Syntax SYNTAX = Syntax.builder()
            .suffix("--")
            .brackets()
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", Argument.NO_PARSE)))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Double> run(Context ctx) {
        MutableValue mutableValue = ((ValueContainer) Lang.instance().sequencer().parse(ctx.getLiteral("VariableName").getString())).resolveValue(ctx);
        Literal before = mutableValue.resolve(ctx).or(0D); // Null values will just be 0
        mutableValue.setDatum(before.sub(Literal.Literals.ONE));

        return Result.<Double>builder().success().result(before.getNumber()).build();
    }
}
