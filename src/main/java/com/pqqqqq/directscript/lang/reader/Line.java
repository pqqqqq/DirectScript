package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

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
    private final String[] words;

    public Line(int absoluteNumber, int scriptNumber, String line) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.line = StringUtil.fullLineTrim(line);

        this.arguments = StringUtil.splitNotInQuotes(this.line.substring(this.line.indexOf(' ') + 1).trim(), ","); // TODO: Better way to do this???
        this.words = StringUtil.splitNotInQuotes(this.line.substring(this.line.indexOf(' ') + 1).trim(), " "); // TODO Also this?
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

    public String[] getWords() {
        return words;
    }

    public int getWordCount() {
        return words.length;
    }

    public String getWord(int i) {
        return words[i];
    }

    public String implodeWord(int start, int end) {
        return StringUtils.join(words, " ", start, end);
    }

    public String implodeWord(int start) {
        return StringUtils.join(words, " ", start, words.length);
    }

    public Literal sequenceArg(ScriptInstance scriptInstance, int i) {
        return scriptInstance.getSequencer().parse(getArg(i));
    }

    public Literal sequenceWord(ScriptInstance scriptInstance, int i) {
        return scriptInstance.getSequencer().parse(getWord(i));
    }
}
