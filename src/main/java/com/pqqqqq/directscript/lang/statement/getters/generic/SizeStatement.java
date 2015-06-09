package com.pqqqqq.directscript.lang.statement.getters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(identifiers = {"size"})
public class SizeStatement implements IStatement<Integer> {

    public StatementResult<Integer> run(ScriptInstance scriptInstance, Line line) {
        int size = line.sequenceArg(scriptInstance, 0).getArray().size();
        return StatementResult.<Integer>builder().success().result(size).literal(size).build();
    }
}
