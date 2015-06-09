package com.pqqqqq.directscript.lang.statement;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;

/**
 * Created by Kevin on 2015-06-02.
 */
public interface IStatement<V> {

    StatementResult<V> run(ScriptInstance scriptInstance, Line line);
}
