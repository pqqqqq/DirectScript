package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ValueContainer;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.data.mutable.MutableValue;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.StringParser;

/**
 * Created by Kevin on 2015-06-08.
 * A statement that sets the value of an existing {@link Variable}
 */
public class SetStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .customPredicate(new Predicate<String>() {

                @Override
                public boolean apply(String input) {
                    String[] split = StringParser.instance().parseSplit(input, " = ");
                    return split.length == 2 && !split[0].trim().isEmpty() && !split[1].trim().isEmpty(); // Basically just check if something exists on both sides of an operator
                }
            })
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", new int[]{Argument.NO_RESOLVE, Argument.CREATE_VARIABLE_IF_NONE})))
            .arguments(Arguments.of(GenericArguments.withNameAndFlags("VariableName", Argument.NO_RESOLVE, Argument.CREATE_VARIABLE_IF_NONE), "=", GenericArguments.withName("Value")))
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result<Object> run(Context ctx) {
        MutableValue mutableValue = ((ValueContainer) ctx.getContainer("VariableName")).resolveValue(ctx); // Set a variable
        Literal value = ctx.getLiteral("Value");
        mutableValue.setDatum(value);

        return Result.builder().success().result(value).build(); // We don't use get here because because we don't want to disturb non-literal data
    }
}
