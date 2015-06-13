package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringParser;
import com.pqqqqq.directscript.lang.util.Utilities;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a line in a script
 */
public class Line {
    @Nonnull
    private final int absoluteNumber;
    @Nonnull
    private final int scriptNumber;
    @Nonnull
    private final String line;
    @Nonnull
    private final String trimmedLine;
    @Nonnull
    private final Statement statement;
    @Nonnull
    private final String[] arguments;

    public Line(int absoluteNumber, int scriptNumber, String line) {
        this(absoluteNumber, scriptNumber, line, true);
    }

    public Line(int absoluteNumber, int scriptNumber, String line, boolean throwError) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.line = Utilities.fullLineTrim(line);

        this.statement = Statements.getStatement(this).orNull();
        checkState(!throwError || this.statement != null, "Unknown line statement: " + this.line);

        if (this.statement != null) {
            if (this.statement.doesUseBrackets()) {
                this.trimmedLine = this.line.substring(this.line.indexOf('(') + 1, this.line.lastIndexOf(')')); // Trim to what's inside brackets
            } else {
                String trimmedLine = this.line.substring(this.line.indexOf(' ') + 1).trim(); // Trim prefix
                trimmedLine = trimmedLine.substring(0, trimmedLine.length() - this.statement.getSuffix().length()).trim(); // Trim suffix
                this.trimmedLine = trimmedLine;
            }

            this.arguments = StringParser.instance().parseSplit(this.trimmedLine, this.statement.getSplitString());
        } else {
            this.trimmedLine = null;
            this.arguments = null;
        }
    }

    @Nonnull
    public int getAbsoluteNumber() {
        return absoluteNumber;
    }

    @Nonnull
    public int getScriptNumber() {
        return scriptNumber;
    }

    @Nonnull
    public String getLine() {
        return line;
    }

    @Nonnull
    public String getTrimmedLine() {
        return trimmedLine;
    }

    @Nonnull
    public Statement getStatement() {
        return statement;
    }

    @Nonnull
    public String[] getArguments() {
        return arguments;
    }

    @Nonnull
    public int getArgCount() {
        return arguments.length;
    }

    @Nonnull
    public String getArg(int index) {
        return arguments[index];
    }

    public Context toContex(ScriptInstance scriptInstance) {
        return new Context(scriptInstance, this);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("absoluteLine", this.absoluteNumber)
                .add("scriptLine", this.scriptNumber)
                .add("line", this.line)
                .add("trimmedLine", this.trimmedLine)
                .add("statement", this.statement.getClass().getName())
                .toString();
    }
}
