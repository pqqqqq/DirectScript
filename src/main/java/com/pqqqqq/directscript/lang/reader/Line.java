package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Literals;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a line in a script
 */
public class Line {
    private final int absoluteNumber;
    private final int scriptNumber;
    private final String line;
    private final Statement statement;

    private Line openingBrace = null;
    private Line closingBrace = null;
    private Block internalBlock = null;

    /**
     * Creates a new line with the corresponding absolute and script number, the string representation of the line
     *
     * @param absoluteNumber the absolute line number (of the file)
     * @param scriptNumber   the script line number
     * @param line           the line string
     */
    public Line(int absoluteNumber, int scriptNumber, String line) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.line = Utilities.fullLineTrim(line);
        this.statement = Statements.getStatement(this.line).orNull();
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
     * Gets the corresponding {@link Statement} attached to this line
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Gets if this {@link Line} is runnable, that is if {@link #getStatement()} is not null
     * @return true if runnable
     */
    public boolean isRunnable() {
        return statement != null;
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
        checkState(isRunnable(), "Unknown line statement: " + this.line);

        String trimmedLine;
        if (this.statement.doesUseBrackets()) {
            trimmedLine = this.line.substring(this.line.indexOf('(') + 1, this.line.lastIndexOf(')')); // Trim to what's inside brackets
        } else {
            trimmedLine = this.line.substring(this.statement.getPrefix().length()).trim(); // Trim prefix

            if (this.statement.getIdentifiers() != null && this.statement.getIdentifiers().length > 0 && !this.statement.getSplitString().isEmpty()) {
                trimmedLine = trimmedLine.substring(trimmedLine.indexOf(this.statement.getSplitString()) + 1).trim(); // Trim identifiers
            }

            trimmedLine = trimmedLine.substring(0, trimmedLine.length() - this.statement.getSuffix().length()).trim(); // Trim suffix
        }

        String[] strargs = this.statement.getSplitString().isEmpty() ? new String[]{trimmedLine} : Lang.instance().stringParser().parseSplit(trimmedLine, this.statement.getSplitString());

        Statement.Argument[] arguments = this.statement.getArguments();
        DataContainer[] containers = new DataContainer[arguments.length];
        int curIndex = 0;

        for (Statement.Argument argument : arguments) {
            if (strargs.length <= curIndex) { // If it goes over, just put empty literals
                containers[curIndex++] = Literals.EMPTY;
                continue;
            }

            String strarg = argument.isRest() ? StringUtils.join(strargs, this.statement.getSplitString(), curIndex, strargs.length) : strargs[curIndex];
            DataContainer litarg = argument.doParse() ? Lang.instance().sequencer().parse(strarg) : Literal.getLiteralBlindly(strarg); // Use doParse boolean
            checkState(argument.isOptional() || !(litarg instanceof Literal) || !((Literal) litarg).isEmpty(), "Argument " + curIndex + "(" + argument.getName() + ") is not optional."); // Use isOptional boolean

            if (argument.isModifier() && (!(litarg instanceof Literal) || !((Literal) litarg).getString().equals(argument.getName()))) { // Use isModifier boolean
                continue; // Basically skip this argument but keep the string
            }

            containers[curIndex++] = litarg;
        }

        return new Context(scriptInstance, this, strargs, containers);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("absoluteLine", this.absoluteNumber)
                .add("scriptLine", this.scriptNumber)
                .add("line", this.line)
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
