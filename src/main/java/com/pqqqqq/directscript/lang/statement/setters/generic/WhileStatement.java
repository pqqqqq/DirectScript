package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-09.
 */
@Statement(identifiers = {"while"}, suffix = "{")
public class WhileStatement implements IStatement {

    public StatementResult run(Line.LineContainer lineContainer) {
        ScriptInstance scriptInstance = lineContainer.getScriptInstance();
        Line line = lineContainer.getLine();

        Line endingWhile = scriptInstance.getScript().lookupEndingLine(line);
        checkNotNull(endingWhile, "Cannot find ending brace of while statement");

        int startLine = line.getScriptNumber() + 1;
        int endLine = endingWhile.getScriptNumber() - 1;

        while (lineContainer.getLiteral(0).getBoolean()) {
            for (int i = startLine; i <= endLine && i < scriptInstance.getScript().getLines().size(); i++) {
                Line whileLine = scriptInstance.getScript().getLines().get(i);
                scriptInstance.getResultMap().put(line, whileLine.toContainer(scriptInstance).run()); // Add to result map
            }
        }

        scriptInstance.setSkipLines(true); // Skip lines since we've already run the code block
        return StatementResult.success();
    }
}
