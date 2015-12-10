package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.collect.Lists;
import com.pqqqqq.directscript.lang.data.Datum;
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

    public RemoveIndexStatement() {
        super(Syntax.builder()
                .identifiers("removeindex")
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_RESOLVE), ",", Argument.from("Index")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        MutableValue mutableValue = ((ValueContainer) ctx.getContainer("VariableName")).resolveValue(ctx);

        List<Datum> array = Lists.newArrayList(mutableValue.getLiteral().getArray()); // Gotta remove unmodifiable property
        array.remove(ctx.getLiteral("Index").getNumber().intValue() - 1);
        mutableValue.setDatum(Literal.fromObject(array));

        return Result.success();
    }
}
