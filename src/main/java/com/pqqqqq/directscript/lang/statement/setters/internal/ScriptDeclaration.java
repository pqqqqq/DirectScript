package com.pqqqqq.directscript.lang.statement.setters.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = {"script"}, suffix = "{", executionTime = Statement.ExecutionTime.COMPILE)
public class ScriptDeclaration implements IStatement<String> {

    public StatementResult<String> run(Line.LineContainer line) {
        String scriptName = line.getLiteral(0).getString();
        return StatementResult.<String>builder().success().result(scriptName).build();
    }
}
