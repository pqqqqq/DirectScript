package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.collect.Lists;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ValueContainer;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.List;

/**
 * Created by Kevin on 2015-11-14.
 */
public class RemoveIndexStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("removeindex")
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", Argument.NO_RESOLVE), ",", GenericArguments.withName("Index")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        MutableValue mutableValue = ((ValueContainer) ctx.getContainer("VariableName")).resolveValue(ctx);

        List<Literal> array = Lists.newArrayList(mutableValue.resolve(ctx).getArray()); // Gotta remove unmodifiable property
        array.remove(ctx.getLiteral("Index").getNumber().intValue() - 1);
        mutableValue.setDatum(Literal.fromObject(array));

        return Result.success();
    }
}
