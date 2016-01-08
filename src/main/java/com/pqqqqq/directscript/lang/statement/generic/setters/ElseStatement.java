package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.exception.MissingBraceException;
import com.pqqqqq.directscript.lang.exception.MissingInternalBlockException;
import com.pqqqqq.directscript.lang.exception.UnknownLineException;
import com.pqqqqq.directscript.lang.reader.Block;
import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-08.
 * A statement that only executes if the 'if' statements above are all false
 */
public class ElseStatement extends Statement {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("} else")
            .suffix("{")
            .brackets()
            .build();

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }

    @Override
    public Result run(Context ctx) {
        ScriptInstance scriptInstance = ctx.getScriptInstance();
        Line line = ctx.getLine();
        Line associatedLine = line.getOpeningBrace().orElseThrow(() -> new MissingBraceException("Unable to find the opening brace '{'."));

        if (scriptInstance.isRuntime()) {
            Result statementResult = scriptInstance.getResultOf(associatedLine);
            if (statementResult == null) {
                return null;
            }

            Optional<Object> result = statementResult.getResult();
            checkState(result.isPresent(), "Reverse lookup for the if statement at line " + associatedLine.getAbsoluteNumber() + " failed");

            Boolean bool = (Boolean) result.get();
            if (!bool) {
                String trimBeginning = line.getLine().substring(6).trim();
                try { // Else if
                    return Line.fromLine(line, trimBeginning.trim()).toContext(scriptInstance).run(); // This line is a completely copy of its parent, except its trimmed line
                } catch (UnknownLineException e) { // Else
                    Block internalBlock = ctx.getLine().getInternalBlock().orElseThrow(() -> new MissingInternalBlockException("Else statements must have internal blocks."));
                    internalBlock.toRunnable(scriptInstance).execute();
                    return Result.builder().success().result(true).build();
                }
            } else {
                return Result.builder().success().result(true).build(); // Build this one as true
            }
        }

        return null;
    }
}
