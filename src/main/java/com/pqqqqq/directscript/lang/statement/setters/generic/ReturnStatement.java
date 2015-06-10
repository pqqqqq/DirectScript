package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(identifiers = {"return", "exit"})
public class ReturnStatement implements IStatement {

    public StatementResult run(Line.LineContainer line) {
        Literal returnValue = line.getLiteral(0).or(Literal.empty());
        line.getScriptInstance().setReturnValue(Optional.of(returnValue));
        return StatementResult.success();
    }
}
