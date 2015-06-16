package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringParser;
import com.pqqqqq.directscript.lang.util.Utilities;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a line in a script
 */
public class Line {
    private final int absoluteNumber;
    private final int scriptNumber;
    private final String line;
    private final String trimmedLine;
    private final Statement statement;
    private final String[] arguments;

    private Line openingBrace = null;
    private Line closingBrace = null;
    private Block internalBlock = null;

    /**
     * Creates a new line with the corresponding absolute and script number, and the string representation of the line, that throws errors
     *
     * @param absoluteNumber the absolute line number (of the file)
     * @param scriptNumber   the script line number
     * @param line           the line string
     */
    public Line(int absoluteNumber, int scriptNumber, String line) {
        this(absoluteNumber, scriptNumber, line, true);
    }

    /**
     * Creates a new line with the corresponding absolute and script number, the string representation of the line and whether it throws errors
     *
     * @param absoluteNumber the absolute line number (of the file)
     * @param scriptNumber   the script line number
     * @param line           the line string
     * @param throwError     whether to throw errors
     */
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

    /**
     * Gets the absolute line number (of the file)
     *
     * @return the absolute line number
     */
    public int getAbsoluteNumber() {
        return absoluteNumber;
    }

    /**
     * Gets the script line number, such that <code>script.getLines().get(getScriptNumber())</code> is this line
     *
     * @return the line number of the script
     */
    public int getScriptNumber() {
        return scriptNumber;
    }

    /**
     * Gets the string representation of this line
     *
     * @return the line string
     */
    public String getLine() {
        return line;
    }

    /**
     * Gets the trimmed line string, without the prefix, identifier and suffix
     *
     * @return the trimmed line
     */
    public String getTrimmedLine() {
        return trimmedLine;
    }

    /**
     * Gets the corresponding {@link Statement} attached to this line
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Gets an array of arguments split by {@link Statement#getSplitString()}
     * @return the string array
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * Gets the number of arguments
     * @return the size of the argument array
     */
    public int getArgCount() {
        return arguments.length;
    }

    /**
     * Gets the string argument at the given index
     * @param index the index to check
     * @return the string argument at the index
     */
    public String getArg(int index) {
        return arguments[index];
    }

    /**
     * Gets the opening brace {@link Line} for this line. In other words, gets the line that associates this line as a closing brace
     * @return the line
     */
    public Line getOpeningBrace() {
        return openingBrace;
    }

    void setOpeningBrace(Line openingBrace) { // Default view
        this.openingBrace = openingBrace;
    }

    /**
     * Gets the closing brace {@link Line} for this line. In other words, gets the line that associates this line as an opening brace
     * @return the line
     */
    public Line getClosingBrace() {
        return closingBrace;
    }

    void setClosingBrace(Line closingBrace) { // Default view
        this.closingBrace = closingBrace;
    }

    /**
     * Gets the internal nested {@link Block} that this {@link Line} initiates
     *
     * @return the nested block
     */
    public Block getInternalBlock() {
        return internalBlock;
    }

    void generateInternalBlock(Block parentBlock) { // Default view
        int startLine = getScriptNumber() + 1;
        int endLine = getClosingBrace().getScriptNumber(); // Don't subtract 1 since subList is exclusive for the end

        int maxStartLine = Math.max(0, startLine);
        int minEndLine = Math.min(parentBlock.getLines().size(), endLine);
        this.internalBlock = new Block(parentBlock.getLines().subList(maxStartLine, minEndLine));
    }

    /**
     * Converts this line into a {@link Context} with the given {@link ScriptInstance}
     * @param scriptInstance the script instance that's running this line
     * @return the context
     */
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

    @Override
    public int hashCode() {
        return Objects.hashCode(absoluteNumber, scriptNumber, line);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Line) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }
}
