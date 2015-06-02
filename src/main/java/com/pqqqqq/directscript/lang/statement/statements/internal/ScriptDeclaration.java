package com.pqqqqq.directscript.lang.statement.statements.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(prefix = "#", identifiers = { "SCRIPT" })
public class ScriptDeclaration implements IStatement<String> {

    public StatementResult<String> run(Line line) {
        Literal scriptName = line.getLiteral(1);

        if (!scriptName.isString()) {
            throw new IllegalArgumentException(line.getWord(1) + " is not a string");
        }

        return StatementResult.<String>builder().success().result((String) scriptName.getValue().get()).build();
    }
}
