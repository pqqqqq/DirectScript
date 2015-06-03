package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.container.ScriptInstance;

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
        // First, check if this is just a plain literal
        Optional<Literal> literal = Literal.getLiteral(sequence);
        if (literal.isPresent()) {
            return literal.get(); // Well that was easy
        }

        // TODO: The rest of sequence
        // Variable check TODO this needs to be altered
        if (sequence.startsWith("$")) {
            return scriptInstance.getVariables().get(sequence.substring(1)).getData();
        }

        throw new IllegalStateException("No coherent sequence could be created from: " + sequence); // If all else fails, throw an exception
    }
}
