package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringParser;
import com.pqqqqq.directscript.lang.util.Utilities;

import javax.annotation.Nonnull;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a line in a script
 */
public class Line {
    @Nonnull
    private final int absoluteNumber;
    @Nonnull
    private final int scriptNumber;
    @Nonnull private final String line;
    @Nonnull
    private final String trimmedLine;
    @Nonnull
    private final Optional<IStatement> istatement;
    @Nonnull
    private final Optional<Statement> statement;
    @Nonnull
    private final String[] arguments;

    public Line(int absoluteNumber, int scriptNumber, String line) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.line = Utilities.fullLineTrim(line);
        this.istatement = Statements.getIStatement(this);
        this.statement = Statements.getStatement(this);

        String trimmedLine = this.line.substring(this.line.indexOf(' ') + 1).trim();
        if (statement.isPresent() && statement.get().suffix() != null) {
            trimmedLine = trimmedLine.substring(0, trimmedLine.length() - statement.get().suffix().length()).trim();
        }

        this.trimmedLine = trimmedLine;
        this.arguments = StringParser.instance().parseSplit(trimmedLine, ","); // TODO: Better way to do this???
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
    public Optional<IStatement> getIStatement() {
        return istatement;
    }

    @Nonnull
    public Optional<Statement> getStatement() {
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
    public String getArg(int i) {
        return arguments[i];
    }

    public Literal sequenceArg(ScriptInstance scriptInstance, int i) {
        return scriptInstance.getSequencer().parse(getArg(i));
    }
}
