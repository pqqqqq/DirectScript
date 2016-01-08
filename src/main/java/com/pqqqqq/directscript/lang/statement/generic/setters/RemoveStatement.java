package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ValueContainer;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Collection;

/**
 * Created by Kevin on 2015-07-11.
 * A statement that removes a KV pair from a map or a value from an array
 */
public class RemoveStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("remove")
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", Argument.NO_RESOLVE), ",", GenericArguments.withName("Key")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        MutableValue mutableValue = ((ValueContainer) ctx.getContainer("VariableName")).resolveValue(ctx);

        Literal container = mutableValue.resolve(ctx);
        Literal key = ctx.getLiteral("Key");

        Collection<Literal> iterable;

        if (container.isMap()) {
            iterable = Maps.newHashMap(container.getMap()).keySet(); // Remove unmodifiability
        } else if (container.isArray()) {
            iterable = Lists.newArrayList(container.getArray());
        } else {
            return Result.failure();
        }

        iterable.removeIf((literal) -> literal.getString().equals(key.getString()));
        mutableValue.setDatum(Literal.fromObject(iterable));
        return Result.success();
    }
}
