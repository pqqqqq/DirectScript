package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.data.container.HolderContainer;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-08.
 * A statement that sets the value of an existing {@link Variable}
 */
public class SetStatement extends Statement<Object> {

    public SetStatement() {
        super(Syntax.builder()
                .identifiers("set")
                .brackets()
                .arguments(Arguments.of(Argument.builder().name("VariableName").parse().build()))
                .arguments(Arguments.of(Argument.builder().name("VariableName").parse().build(), "=", Argument.from("Value")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        LiteralHolder literalHolder = ((HolderContainer) Lang.instance().sequencer().parse(ctx.getLine(), ctx.getLiteral("VariableName").getString())).resolveHolder(ctx.getScriptInstance());
        Literal value = ctx.getLiteral("Value").copy(); // We want a copied version

        literalHolder.setData(value);
        return Result.builder().success().result(value.getValue().orNull()).build();
    }
}
