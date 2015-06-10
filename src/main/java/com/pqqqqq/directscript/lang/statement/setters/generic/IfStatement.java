package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-04.
 */
@Statement(identifiers = {"if"}, suffix = "{")
public class IfStatement implements IStatement<Boolean> {

    public StatementResult<Boolean> run(Line.LineContainer line) {
        boolean result = line.getLiteral(0).getBoolean();
        if (!result) {
            line.getScriptInstance().setSkipLines(true);
        }

        return StatementResult.<Boolean>builder().success().result(result).build();
    }
}
