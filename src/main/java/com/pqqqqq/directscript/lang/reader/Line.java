package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.util.Utilities;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
        this.words = parseWords();
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

    public int getWordCount() {
        return words.length;
    }

    public String getWord(int i) {
        return words[i];
    }

    public Literal getLiteral(ScriptInstance scriptInstance, int i) {
        return scriptInstance.getSequencer().parse(getWord(i));
    }

    private String[] parseWords() { // Ignore spaces in quotes
        List<String> words = new ArrayList<String>();
        String buffer = "";
        boolean quotes = false;
        boolean escape = false;

        for (char ch : this.line.toCharArray()) {
            if (escape) {
                escape = false;
            } else if (ch == '\\') {
                escape = true;
            }

            if (!escape && ch == '"') {
                quotes = !quotes;
            } else if (ch == ' ') {
                if (!quotes && !buffer.trim().isEmpty()) {
                    words.add(buffer.trim());
                    buffer = "";
                    continue;
                }
            }

            buffer += ch;
        }

        if (!buffer.trim().isEmpty()) {
            words.add(buffer.trim());
        }

        return words.toArray(new String[words.size()]);
    }
}
