package com.pqqqqq.directscript.lang.statement.getters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

import java.util.List;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(identifiers = {"join"})
public class JoinStatement implements IStatement<String> {

    public StatementResult<String> run(Line.LineContainer line) {
        List<Variable> array = line.getLiteral(0).getArray();
        String joinString = line.getLiteral(1).getString();
        int start = line.getLiteral(2).or(0).getNumber().intValue();
        int end = line.getLiteral(3).or(array.size()).getNumber().intValue();

        String joined = "";
        for (int i = start; i < end && i < array.size(); i++) {
            joined += array.get(i).getData().getString() + joinString;
        }

        joined = joined.isEmpty() ? joined : joined.substring(0, joined.length() - joinString.length());
        return StatementResult.<String>builder().success().result(joined).literal(joined).build();
    }
}
