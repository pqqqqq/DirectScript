package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.util.Utilities;

/**
 * Created by Kevin on 2015-06-02.
 * A sequencer parses values that are dependent on an environment and/or the variables within it. It
 * may also parse a combination of literals. They are parsed to {@link Literal}s
 */
public class Sequencer {
    private ScriptInstance scriptInstance;

    private Sequencer(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    public static Sequencer instance(ScriptInstance scriptInstance) {
        return new Sequencer(scriptInstance);
    }

    public Literal parse(String sequence) {
        // TODO: The rest of sequence
        Literal result = Literal.empty();

        String[] segments = Utilities.splitNotInQuotes(sequence.trim(), "+"); // Split into segments
        for (String segment : segments) {
            segment = segment.trim();

            if (segment.startsWith("$")) { // Check if it's a variable
                result = result.addOrSet(scriptInstance.getVariables().get(segment.substring(1)).getData());
                continue;
            } else { // Check plain data
                Optional<Literal> literal = Literal.getLiteral(segment);
                if (literal.isPresent()) {
                    result = result.addOrSet(literal.get()); // Well that was easy
                    continue;
                }
            }

            throw new IllegalStateException("No coherent sequence could be created from: " + segment + " in: " + sequence); // If all else fails, throw an exception
        }

        return result;
    }
}
