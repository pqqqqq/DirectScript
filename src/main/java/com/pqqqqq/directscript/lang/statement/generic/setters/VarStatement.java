package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A statement that creates a new {@link Variable} in the {@link Environment}
 */
public class VarStatement extends Statement<Object> {

    public VarStatement() {
        super(Syntax.builder()
                .identifiers("var")
                .brackets()
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_PARSE)))
                .arguments(Arguments.of(Argument.from("VariableName", Argument.NO_PARSE), "=", Argument.from("Value")))
                .build());
    }

    @Override
    public Result<Object> run(Context ctx) {
        boolean isFinal = false, parse = false;
        Environment environment = ctx.getScriptInstance().getTop();
        String name = "";

        String[] words = Lang.instance().stringParser().parseSplit(ctx.getLiteral("VariableName").getString(), " ");
        for (String word : words) {
            if (word.equals("final")) {
                isFinal = true;
            } else if (word.equals("parse")) {
                parse = true;

                // TODO: Variable visibility masking fixes?
            } else if (word.equals("local")) {
                environment = ctx.getScript().getScriptsFile();
            } else if (word.equals("global")) {
                environment = Lang.instance();
            } else if (word.equals("public")) {
                environment = DirectScript.instance();
            } else {
                name += word + " ";
                if (!parse) {
                    break;
                }
            }
        }

        name = name.trim();

        checkState(!name.isEmpty(), "Improper variable declaration");
        if (parse) {
            name = Lang.instance().sequencer().parse(ctx.getLine(), name).resolve(ctx.getScriptInstance()).getString();
        }

        Literal value = ctx.getLiteral("Value").copy();
        environment.addVariable(new Variable(name, value, isFinal));
        return Result.builder().success().result(value.getValue().orNull()).build();
    }
}
