package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.setters.internal.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 */
@Statement(identifiers = {"} else"}, suffix = "{", useBrackets = false)
public class ElseStatement extends Termination {

    @Override
    public StatementResult run(Line.LineContainer lineContainer) {
        ScriptInstance scriptInstance = lineContainer.getScriptInstance();
        Line line = lineContainer.getLine();

        Line associatedLine = scriptInstance.getScript().lookupStartingLine(line);
        checkNotNull(associatedLine, "Unknown termination sequence");

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
                    return truncatedLine.toContainer(scriptInstance).run();
                } catch (NullPointerException e) {
                }
            } else {
                scriptInstance.setSkipLines(true); // Skip lines if previously one was true
            }
        }

        return StatementResult.success();
    }
}
