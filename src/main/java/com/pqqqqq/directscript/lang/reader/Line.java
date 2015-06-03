package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
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
    private final String[] arguments;

    public Line(int absoluteNumber, int scriptNumber, String line) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.line = Utilities.fullLineTrim(line);
        this.arguments = Utilities.splitNotInQuotes(this.line.substring(this.line.indexOf(' ') + 1).trim(), ","); // TODO: Better way to do this???
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

    public String[] getArguments() {
        return arguments;
    }

    public int getArgCount() {
        return arguments.length;
    }

    public String getArg(int i) {
        return arguments[i];
    }

    public Literal sequence(ScriptInstance scriptInstance, int i) {
        return scriptInstance.getSequencer().parse(getArg(i));
    }
}
