package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ArrayContainer;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.data.container.UnresolvableContainer;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringParser;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
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
    private int depth = 0;

    private List<ContextPossibility> contextPossibilities = null;

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
        this.line = StringParser.instance().trim(line);
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
     * <p>Gets the depth of this line, or how far from the main script block this line is</p>
     * <p>This value is an unsigned integer where 0 represents the main block, and anything above is an upper block</p>
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the depth of this line
     *
     * @param depth the new depth
     * @see #getDepth()
     */
    public void setDepth(int depth) {
        this.depth = Math.max(0, depth);
    }

    /**
     * Gets the corresponding {@link Statement} attached to this line
     *
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Gets if this {@link Line} is runnable, that is if {@link #getStatement()} is not null
     *
     * @return true if runnable
     */
    public boolean isRunnable() {
        return statement != null;
    }

    /**
     * Gets the opening brace {@link Line} for this line. In other words, gets the line that associates this line as a closing brace
     *
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
     *
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

    /**
     * Sets the internal nested {@link Block} that this {@link Line} initiates
     *
     * @param internalBlock the internal block
     */
    public void setInternalBlock(Block internalBlock) {
        this.internalBlock = internalBlock;
    }

    void generateInternalBlock(Block parentBlock) { // Default view
        int startLine = getScriptNumber() + 1;
        int endLine = getClosingBrace().getScriptNumber(); // Don't subtract 1 since subList is exclusive for the end

        int maxStartLine = Math.max(0, startLine);
        int minEndLine = Math.min(parentBlock.getLines().size(), endLine);
        this.internalBlock = new Block(getDepth() + 1, parentBlock.getLines().subList(maxStartLine, minEndLine));
    }

    /**
     * Converts this line into a {@link Context} with the given {@link ScriptInstance}
     *
     * @param scriptInstance the script instance that's running this line
     * @return the context
     */
    public Context toContext(ScriptInstance scriptInstance) {
        if (contextPossibilities == null) {
            parse();
        }

        return new Context(scriptInstance, this, checkNotNull(this.contextPossibilities, "Possibilities"));
    }

    private void parse() {
        checkState(isRunnable(), "Unknown line statement: " + this.line);
        this.contextPossibilities = new ArrayList<>();

        Statement.Syntax syntax = this.statement.getSyntax();

        String trimmedLine;
        if (syntax.getCustomPredicate().isPresent()) {
            trimmedLine = this.line;
        } else {
            if (syntax.doesUseBrackets()) {
                trimmedLine = this.line.substring(this.line.indexOf('(') + 1, this.line.lastIndexOf(')')); // Trim to what's inside brackets
            } else {
                trimmedLine = StringUtils.removeStart(this.line, syntax.getPrefix()); // Trim prefix

                if (syntax.getIdentifiers() != null && syntax.getIdentifiers().length > 0) {
                    for (String identifier : syntax.getIdentifiers()) {
                        if (trimmedLine.startsWith(identifier)) {
                            trimmedLine = StringUtils.removeStart(trimmedLine, identifier); // Trim identifier
                            break;
                        }
                    }
                }

                trimmedLine = StringUtils.removeEnd(trimmedLine, syntax.getSuffix()); // Trim suffix
            }
        }

        argumentLoop:
        for (Statement.Arguments args : syntax.getArguments()) {
            String trimmedLineClone = trimmedLine;
            String[] delimiters = args.getDelimiters();
            List<String> strargs = new ArrayList<>();

            for (String delimiter : delimiters) {
                int index = Lang.instance().stringParser().indexOf(trimmedLineClone, delimiter);
                if (index == -1) {
                    continue argumentLoop;
                } else {
                    strargs.add(trimmedLineClone.substring(0, index).trim());
                    trimmedLineClone = trimmedLineClone.substring(index + delimiter.length());
                }
            }

            if (!trimmedLineClone.trim().isEmpty()) {
                String[] split = Lang.instance().stringParser().parseSplit(trimmedLineClone.trim(), ","); // Account for conjugated lists
                if (split != null) {
                    strargs.addAll(Arrays.asList(split));
                }
            }

            if (strargs.size() >= args.getArguments().length) {
                this.contextPossibilities.add(new ContextPossibility().setArgumentSet(args).setStrargs(strargs));
            }
        }

        List<ContextPossibility> persist = new ArrayList<>();
        for (ContextPossibility contextPossibility : this.contextPossibilities) {
            String[] strargs = contextPossibility.getStrargs();

            checkState(contextPossibility.getArgumentSet() != null && strargs != null, "Invalid argument syntax");
            Map<Statement.Argument, DataContainer> containers = new HashMap<>();

            boolean conjugation = false;
            for (Statement.Argument argument : contextPossibility.getArgumentSet().getArguments()) {
                int currentIndex = containers.size();
                if (!argument.doConjugateToList()/* || currentIndex == (strargs.length - 1)*/) { // Conjugation is irrelevant if there's only one argument left
                    containers.put(argument, getDataContainer(argument, strargs[currentIndex]));
                } else {
                    conjugation = true;
                    List<DataContainer> dataContainerList = new ArrayList<>();
                    for (int i = currentIndex; i < strargs.length; i++) { // From current position to the end
                        dataContainerList.add(getDataContainer(argument, strargs[i]));
                    }

                    containers.put(argument, new ArrayContainer(dataContainerList)); // Put an array container
                    break;
                }
            }

            contextPossibility.setContainers(containers); // Set new containers
            if (!(!conjugation && contextPossibility.getStrargs().length != contextPossibility.getContainers().size())) {
                persist.add(contextPossibility);
            }
        }

        this.contextPossibilities = persist;
    }

    private DataContainer getDataContainer(Statement.Argument argument, String strarg) { // Method to avoid duplicate code
        DataContainer dataContainer = (argument.doParse() ? Lang.instance().sequencer().parse(strarg) : Literal.fromObject(strarg)); // Use doParse boolean
        if (!argument.doResolve()) {
            dataContainer = new UnresolvableContainer(dataContainer); // Use doResolve boolean
        }

        return dataContainer;
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

    protected static class ContextPossibility { // This class is hidden completely
        private Statement.Arguments argumentSet = null;
        private String[] strargs = null;
        private Map<Statement.Argument, DataContainer> containers = null;

        protected ContextPossibility() {
        }

        protected Statement.Arguments getArgumentSet() {
            return argumentSet;
        }

        protected ContextPossibility setArgumentSet(Statement.Arguments argumentSet) {
            this.argumentSet = argumentSet;
            return this;
        }

        protected String[] getStrargs() {
            return strargs;
        }

        protected ContextPossibility setStrargs(String[] strargs) {
            this.strargs = strargs;
            return this;
        }

        protected ContextPossibility setStrargs(Collection<String> strargs) {
            this.strargs = strargs.toArray(new String[strargs.size()]);
            return this;
        }

        protected Map<Statement.Argument, DataContainer> getContainers() {
            return containers;
        }

        protected ContextPossibility setContainers(Map<Statement.Argument, DataContainer> containers) {
            this.containers = containers;
            return this;
        }
    }
}
