package com.pqqqqq.directscript.lang.statement.getters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(identifiers = {"split"})
public class SplitStatement implements IStatement<String[]> {

    public StatementResult<String[]> run(ScriptInstance scriptInstance, Line line) {
        String[] split = line.sequenceArg(scriptInstance, 0).getString().split(line.sequenceArg(scriptInstance, 1).getString());
        return StatementResult.<String[]>builder().success().result(split).literal(split).build();
    }
}
