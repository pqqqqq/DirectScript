package com.pqqqqq.directscript.lang.statement.getters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(identifiers = {"trim"})
public class TrimStatement implements IStatement<String> {

    public StatementResult<String> run(Line.LineContainer line) {
        String trim = line.getLiteral(0).getString().trim();
        return StatementResult.<String>builder().result(trim).literal(trim).build();
    }
}
