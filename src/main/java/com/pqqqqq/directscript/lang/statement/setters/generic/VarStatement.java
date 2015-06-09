package com.pqqqqq.directscript.lang.statement.setters.generic;

import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.util.StringParser;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 */
@Statement(identifiers = {"var"})
public class VarStatement implements IStatement {

    public StatementResult run(ScriptInstance scriptInstance, Line line) {
        String[] words = StringParser.instance().parseSplit(line.getTrimmedLine(), " "); // Split spaces

        boolean isFinal = false;
        Literal value = Literal.empty();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            // Check modifiers first (eg final)
            if (word.equals("final")) {
                isFinal = true;
            } else {
                // Done with modifiers, start with name and value
                checkState(Variable.namePattern().matcher(word).matches(), "This variable name has illegal characters (only alphanumeric)");
                checkState(!scriptInstance.getVariables().containsKey(word), "A variable with this name already exists");

                if (words.length > i) {
                    String EQUALS = words[i + 1];
                    checkState(EQUALS.equals("="), "Unknown word: " + EQUALS);

                    value = scriptInstance.getSequencer().parse(StringUtils.join(words, " ", i + 2, words.length));
                }

                scriptInstance.getVariables().put(word, new Variable(word, value, isFinal));
                return StatementResult.success();
            }
        }

        throw new IllegalArgumentException("Improper variable declaration: " + line.getLine());
    }
}
