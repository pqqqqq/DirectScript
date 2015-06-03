package com.pqqqqq.directscript.lang.statement;

import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.reader.Line;

/**
 * Created by Kevin on 2015-06-02.
 */
public interface IStatement<T> {

    StatementResult<T> run(Script script, Line line);
}
