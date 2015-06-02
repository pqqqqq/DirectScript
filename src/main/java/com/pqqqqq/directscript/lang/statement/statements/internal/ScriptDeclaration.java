package com.pqqqqq.directscript.lang.statement.statements.internal;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(prefix = "#", identifiers = { "SCRIPT" })
public class ScriptDeclaration implements IStatement<String> {

    public StatementResult<String> run(String line) {
        String[] split = line.split(" ");

        if (split.length < 2) {
            return StatementResult.failure();
        }

        return StatementResult.<String>builder().success().result(split[1]).build();
    }
}
