package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;

/**
 * Created by Kevin on 2015-06-02.
 * A sequence parses values that are dependent on an environment and/or the variables within it. It
 * may also parse a combination of literals. They are parsed to {@link Literal}s
 */
public class Sequence {
    private static final Sequence INSTANCE = new Sequence();
    private Sequence() {
    }

    public static Sequence instance() {
        return INSTANCE;
    }

    public Literal parse(String sequence) {
        // First, check if this is just a plain literal
        Optional<Literal> literal = Literal.getLiteral(sequence);
        if (literal.isPresent()) {
            return literal.get(); // Well that was easy
        }

        // TODO: The rest of sequence

        throw new IllegalStateException("No coherent sequence could be created from: " + sequence); // If all else fails, throw an exception
    }
}
