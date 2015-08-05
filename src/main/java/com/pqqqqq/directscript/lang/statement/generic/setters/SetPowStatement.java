package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.container.HolderContainer;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.StringParser;

/**
 * Created by Kevin on 2015-06-08.
 * A statement that sets the value of an existing {@link Variable} by raising an exponent by its current value
 */
public class SetPowStatement extends Statement<Object> {

    public SetPowStatement() {
        super(Syntax.builder()
                .customPredicate(new Predicate<String>() {

                    @Override
                    public boolean apply(String input) {
                        String[] split = StringParser.instance().parseSplit(input, " ^= ");
                        return split.length == 2 && !split[0].trim().isEmpty() && !split[1].trim().isEmpty(); // Basically just check if something exists on both sides of an operator
                    }
                })
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_RESOLVE)))
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_RESOLVE), "^=", Argument.from("Value")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        LiteralHolder literalHolder = ((HolderContainer) ctx.getContainer("VariableName")).resolveHolder(ctx.getScriptInstance());
        Literal value = ctx.getLiteral("Value").copy(); // We want a copied version

        literalHolder.setData(literalHolder.getData().pow(value));
        return Result.builder().success().result(literalHolder.getData()).build();
    }
}
