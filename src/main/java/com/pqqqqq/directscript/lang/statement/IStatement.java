package com.pqqqqq.directscript.lang.statement;

/**
 * Created by Kevin on 2015-06-02.
 */
public interface IStatement<T> {

    StatementResult<T> run(String line);
}
