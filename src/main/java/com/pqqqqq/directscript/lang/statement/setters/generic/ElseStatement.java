package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.setters.internal.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 */
@Statement(identifiers = {"} else"}, suffix = "{")
public class ElseStatement extends Termination {

    @Override
    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        Line associatedLine = scriptInstance.getScript().lookupStartingLine(line);
        if (associatedLine == null) {
            throw new IllegalStateException("Unknown termination sequence");
        }

        IStatement iStatement = associatedLine.getIStatement();
        if (scriptInstance.getCause() != Causes.COMPILE) {
            StatementResult statementResult = scriptInstance.getResultOf(associatedLine);
            if (statementResult == null) {
                return StatementResult.success();
            }

            Optional<Object> result = statementResult.getResult();
            checkState(result.isPresent(), "Reverse lookup for the if statement at line " + associatedLine.getAbsoluteNumber() + " failed");

            Boolean bool = (Boolean) result.get();
            if (!bool) {
                scriptInstance.setSkipLines(false); // Turn off skipping lines for this
                String trimBeginning = line.getLine().substring(6);
                try {
                    Line truncatedLine = new Line(line.getAbsoluteNumber(), line.getAbsoluteNumber(), trimBeginning);
                    return truncatedLine.getIStatement().run(scriptInstance, truncatedLine);
                } catch (NullPointerException e) {
                }
            } else {
                scriptInstance.setSkipLines(true); // Skip lines if previously one was true
            }
        }

        return StatementResult.success();
    }
}
