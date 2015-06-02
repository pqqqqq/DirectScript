package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Sequence;
import com.pqqqqq.directscript.util.Utilities;

import javax.annotation.Nonnull;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a line in a script
 */
public class Line {
    private final int absoluteNumber;
    private final int scriptNumber;
    @Nonnull private final String line;
    private final String[] words;

    public Line(int absoluteNumber, int scriptNumber, String line) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.line = Utilities.fullLineTrim(line);
        this.words = this.line.split(" ");
    }

    public int getAbsoluteNumber() {
        return absoluteNumber;
    }

    public int getScriptNumber() {
        return scriptNumber;
    }

    public String getLine() {
        return line;
    }

    public String[] getWords() {
        return words;
    }

    public String getWord(int i) {
        return words[i];
    }

    public Literal getLiteral(int i) {
        return Sequence.instance().parse(getWord(i));
    }
}
