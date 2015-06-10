package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringParser;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.entity.player.Player;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

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
    private final IStatement istatement;
    @Nonnull
    private final Statement statement;
    @Nonnull
    private final String[] arguments;

    public Line(int absoluteNumber, int scriptNumber, String line) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.line = Utilities.fullLineTrim(line);
        this.istatement = Statements.getIStatement(this).orNull();
        this.statement = Statements.getStatement(this).orNull();

        checkNotNull(this.istatement, "Unknown line statement: " + this.line);
        checkNotNull(this.statement, "Unknown line statement: " + this.line);

        if (this.statement.useBrackets()) {
            this.trimmedLine = this.line.substring(this.line.indexOf('(') + 1, this.line.lastIndexOf(')')); // Trim to what's inside brackets
        } else {
            String trimmedLine = this.line.substring(this.line.indexOf(' ') + 1).trim(); // Trim prefix
            trimmedLine = trimmedLine.substring(0, trimmedLine.length() - this.statement.suffix().length()).trim(); // Trim suffix
            this.trimmedLine = trimmedLine;
        }
        this.arguments = StringParser.instance().parseSplit(this.trimmedLine, ","); // TODO: Better way to do this???
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
    public IStatement getIStatement() {
        return istatement;
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

    public LineContainer toContainer(ScriptInstance scriptInstance) {
        return new LineContainer(scriptInstance, this);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("absoluteLine", this.absoluteNumber)
                .add("scriptLine", this.scriptNumber)
                .add("line", this.line)
                .add("trimmedLine", this.trimmedLine)
                .add("statement", this.istatement.getClass().getName())
                .toString();
    }

    public static class LineContainer {
        private final ScriptInstance scriptInstance;
        private final Line line;
        private final Literal[] literals;

        LineContainer(ScriptInstance scriptInstance, Line line) {
            this.scriptInstance = scriptInstance;
            this.line = line;

            if (line.getStatement().useBrackets()) {
                this.literals = new Literal[line.getArgCount()];
                for (int i = 0; i < this.literals.length; i++) {
                    this.literals[i] = scriptInstance.getSequencer().parse(line.getArg(i));
                }
            } else {
                this.literals = null;
            }
        }

        public ScriptInstance getScriptInstance() {
            return scriptInstance;
        }

        public Line getLine() {
            return line;
        }

        public Literal[] getLiterals() {
            return literals;
        }

        public Literal getLiteral(int index) {
            return literals[index];
        }

        public int getLiteralCount() {
            return literals.length;
        }

        public StatementResult run() {
            return line.getIStatement().run(this);
        }

        // Convenience stuff
        public Optional<Player> getPlayerOrCauser(int index) {
            Optional<Player> causedBy = this.scriptInstance.getCausedBy();
            Literal literal = this.literals[index];
            return (literal.isEmpty() ? causedBy : literal.getPlayer());
        }
    }
}
