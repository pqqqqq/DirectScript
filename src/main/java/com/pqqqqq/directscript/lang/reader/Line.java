package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Objects;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.ArrayContainer;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.data.container.UnresolvableContainer;
import com.pqqqqq.directscript.lang.data.container.VariableContainer;
import com.pqqqqq.directscript.lang.exception.UnknownLineException;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Compartment;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringParser;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a line in a script
 */
public class Line {
    private final int absoluteNumber;
    private final int scriptNumber;
    private final Content content;
    private final Script script;
    private final Block blockContainer;

    private final Optional<Integer> openingBraceLine;
    private final Optional<Integer> closingBraceLine;
    private final Optional<Block> internalBlock;

    private Line(int absoluteNumber, int scriptNumber, Content content, Script script, Block blockContainer, Integer openingBraceLine, Integer closingBraceLine, Block internalBlock) {
        this.absoluteNumber = absoluteNumber;
        this.scriptNumber = scriptNumber;
        this.content = content;
        this.script = script;
        this.blockContainer = blockContainer;

        this.openingBraceLine = Optional.ofNullable(openingBraceLine);
        this.closingBraceLine = Optional.ofNullable(closingBraceLine);
        this.internalBlock = Optional.ofNullable(internalBlock);

        // Add the line to the script, and if not the same, the block container
        this.script.getLines().add(this);
        if (this.script != this.blockContainer) {
            this.blockContainer.getLines().add(this);
        }
    }

    private Line(Line template, String line) {
        this.absoluteNumber = template.getAbsoluteNumber();
        this.scriptNumber = template.getScriptNumber();
        this.content = new Content(line); // New content
        this.script = template.getScript();
        this.blockContainer = template.getBlockContainer();

        this.openingBraceLine = (template.getOpeningBrace().isPresent() ? Optional.of(template.getOpeningBrace().get().getScriptNumber()) : Optional.empty());
        this.closingBraceLine = (template.getClosingBrace().isPresent() ? Optional.of(template.getClosingBrace().get().getScriptNumber()) : Optional.empty());
        this.internalBlock = template.getInternalBlock();
    }

    /**
     * Creates a new {@link Builder Line Builder}
     * @return the new line builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Line} segment from the template and with the new line
     * @param template the template
     * @param line the line segment
     * @return the new line instance
     */
    public static Line fromLine(Line template, String line) {
        return new Line(template, line);
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
     * Gets the {@link Content} for this line
     * @return the content
     */
    public Content getContent() {
        return content;
    }

    /**
     * Gets the {@link Script} for this line
     * @return the script
     */
    public Script getScript() {
        return script;
    }

    /**
     * Gets the string representation of this line
     *
     * @return the line string
     * @see Content#getLine()
     */
    public String getLine() {
        return getContent().getLine();
    }

    /**
     * Gets the corresponding {@link Statement} attached to this line
     *
     * @return the statement
     * @see Content#getStatement()
     */
    public Statement getStatement() {
        return getContent().getStatement();
    }

    /**
     * Gets the {@link Block containing block}, where this {@link Line} resides
     * @return the parent block
     */
    public Block getBlockContainer() {
        return blockContainer;
    }

    /**
     * Gets the depth offset of this line
     *
     * @return the depth offset
     * @see Block#getDepthOffset()
     */
    public int getDepthOffset() {
        return getBlockContainer().getDepthOffset();
    }

    /**
     * Gets if this {@link Line} is runnable, that is if {@link #getStatement()} is not null
     *
     * @return true if runnable
     */
    public boolean isRunnable() {
        return getStatement() != null;
    }

    /**
     * Gets the {@link Optional} opening brace {@link Line} for this line. In other words, gets the line that associates this line as a closing brace
     *
     * @return the line
     */
    public Optional<Line> getOpeningBrace() {
        if (!openingBraceLine.isPresent()) {
            return Optional.empty();
        }

        return Optional.ofNullable(getScript().getLines().get(openingBraceLine.get()));
    }

    /**
     * Gets the {@link Optional} closing brace {@link Line} for this line. In other words, gets the line that associates this line as an opening brace
     *
     * @return the line
     */
    public Optional<Line> getClosingBrace() {
        if (!closingBraceLine.isPresent()) {
            return Optional.empty();
        }

        return Optional.ofNullable(getScript().getLines().get(closingBraceLine.get()));
    }

    /**
     * Gets the {@link Optional} internal nested {@link Block} that this {@link Line} initiates
     *
     * @return the nested block
     */
    public Optional<Block> getInternalBlock() {
        return internalBlock;
    }

    /**
     * Converts this line into a {@link Context} with the given {@link ScriptInstance}
     *
     * @param scriptInstance the script instance that's running this line
     * @return the context
     */
    public Context toContext(ScriptInstance scriptInstance) {
        return new Context(scriptInstance, this, content.getContextPossibilities());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("absoluteLine", this.absoluteNumber)
                .add("scriptLine", this.scriptNumber)
                .add("line", getLine())
                .add("statement", getStatement().getClass().getName())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(absoluteNumber, scriptNumber, getLine());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Line) {
            return hashCode() == obj.hashCode();
        }
        return false;
    }

    /**
     * A class containing the line's content, including its String representation and {@link Statement}
     */
    public static class Content {
        private final String line;
        private final Statement statement;
        private final List<ContextPossibility> contextPossibilities;

        Content(String line) {
            this.line = line;
            this.statement = Statements.getStatement(this.line).orElseThrow(() -> new UnknownLineException("'%s' cannot be identified.", this.line));

            // PARSING
            List<ContextPossibility> contextPossibilities = new ArrayList<>();
            Statement.Syntax syntax = getStatement().getSyntax();

            String trimmedLine;
            if (syntax.getCustomPredicate().isPresent()) {
                trimmedLine = getLine();
            } else {
                if (syntax.doesUseBrackets()) {
                    trimmedLine = getLine().substring(getLine().indexOf('(') + 1, getLine().lastIndexOf(')')); // Trim to what's inside brackets
                } else {
                    trimmedLine = StringUtils.removeStart(getLine(), syntax.getPrefix()); // Trim prefix

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

            final String trimmedLineFinal = trimmedLine;
            Consumer<Compartment> consumer = (compartment) -> {
                argumentLoop:
                for (Statement.Arguments args : compartment.getArgumentsArray()) {
                    String trimmedLineClone = trimmedLineFinal;
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
                        contextPossibilities.add(new ContextPossibility().setArgumentSet(args).setStrargs(strargs).setCompartment(compartment));
                    }
                }
            };

            consumer.accept(this.statement);
            for (Object obj : this.statement.getCompartments()) { // TODO Why the hell do I need to use object here?!
                consumer.accept((Compartment) obj);
            }

            List<ContextPossibility> persist = new ArrayList<>();
            for (ContextPossibility contextPossibility : contextPossibilities) {
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

                        containers.put(argument, new ArrayContainer(dataContainerList, true)); // Put an array container (concatenated)
                        break;
                    }
                }

                contextPossibility.setContainers(containers); // Set new containers
                if (!(!conjugation && contextPossibility.getStrargs().length != contextPossibility.getContainers().size())) {
                    persist.add(contextPossibility);
                }
            }

            this.contextPossibilities = checkNotNull(persist, "There are no context possibilities for this");
        }

        /**
         * Gets the string version of the line
         *
         * @return the line
         */
        public String getLine() {
            return line;
        }

        /**
         * Gets the {@link Statement}
         *
         * @return the statement
         */
        public Statement getStatement() {
            return statement;
        }

        protected Context toContext(ScriptInstance scriptInstance) {
            return new Context(scriptInstance, null, this, getContextPossibilities());
        }

        List<ContextPossibility> getContextPossibilities() {
            return contextPossibilities;
        }

        private DataContainer getDataContainer(Statement.Argument argument, String strarg) { // Method to avoid duplicate code
            DataContainer dataContainer = (argument.doParse() ? Lang.instance().sequencer().parse(strarg) : Literal.fromObject(strarg)); // Use doParse boolean
            if (argument.doCreateVariable() && dataContainer instanceof VariableContainer) { // Create default variable
                dataContainer = new VariableContainer(((VariableContainer) dataContainer).getVariableName(), true);
            }

            if (!argument.doResolve()) { // Don't resolve
                dataContainer = new UnresolvableContainer(dataContainer); // Use doResolve boolean
            }

            return dataContainer;
        }
    }

    /**
     * <p>The {@link Line} builder class.</p>
     * <p>Each {@link Builder} instance pertains to only one {@link Line} instance.</p>
     * <p>Once {@link Builder#build() built}, the builder can no longer create new instances.</p>
     * <p>Subsequent builds will only return the previously built instance.</p>
     */
    public static class Builder {
        private Integer absoluteLineNumber = null;
        private Integer scriptLineNumber = null;
        private String line = null;
        private Script script = null;
        private Block blockContainer = null;
        private Integer openingBraceLine = null;
        private Integer closingBraceLine = null;

        private Block internalBlock = null;
        private Content content = null;

        private Line created = null;

        private Builder() { // Private
        }

        /**
         * Sets the absolute line number of the line, i.e. the line of the file
         *
         * @param absoluteLineNumber the absolute line number
         * @return this builder, for chaining
         * @see Line#getAbsoluteNumber()
         */
        public Builder absoluteLine(int absoluteLineNumber) {
            this.absoluteLineNumber = absoluteLineNumber;
            return this;
        }

        /**
         * Gets the current absolute line number
         *
         * @return the file line number
         */
        public Integer getAbsoluteLineNumber() {
            return absoluteLineNumber;
        }

        /**
         * Sets the script line number of the line, i.e. the lines ran at runtime
         *
         * @param scriptLineNumber the script line number
         * @return this builder, for chaining
         * @see Line#getScriptNumber()
         */
        public Builder scriptLine(int scriptLineNumber) {
            this.scriptLineNumber = scriptLineNumber;
            return this;
        }

        /**
         * Gets the current script line number
         *
         * @return the script line #
         */
        public Integer getScriptLineNumber() {
            return scriptLineNumber;
        }

        /**
         * Sets the string version of this line
         *
         * @param line the line string
         * @return this builder, for chaining
         * @see Line#getLine()
         */
        public Builder line(String line) {
            this.line = StringParser.instance().trim(line);
            return this;
        }

        /**
         * Gets the line string present in this builder, as affected by {@link StringParser#trim(String)} during {@link #line(String)}
         *
         * @return the trimmed line
         */
        public String getLine() {
            return line;
        }

        /**
         * Sets the {@link Script} of this line
         *
         * @param script the script
         * @return this builder, for chaining
         * @see Line#getScript()
         */
        public Builder script(Script script) {
            this.script = script;
            return this;
        }

        /**
         * Gets the {@link Script}
         *
         * @return the script
         */
        public Script getScript() {
            return script;
        }

        /**
         * Sets the containing {@link Block} for this line
         *
         * @param blockContainer the containing block
         * @return this builder, for chaining
         * @see Line#getBlockContainer()
         */
        public Builder block(Block blockContainer) {
            this.blockContainer = blockContainer;
            return this;
        }

        /**
         * Gets the {@link Block} container
         *
         * @return the containing block
         */
        public Block getBlockContainer() {
            return blockContainer;
        }

        /**
         * Sets the opening brace line
         *
         * @param openingBraceLine the opening brace
         * @return this builder, for chaining
         * @see Line#getOpeningBrace()
         */
        public Builder openingBrace(Integer openingBraceLine) {
            this.openingBraceLine = openingBraceLine;
            return this;
        }

        /**
         * Gets the opening brace line
         *
         * @return the opening brace line
         */
        public Integer getOpeningBraceLine() {
            return openingBraceLine;
        }

        /**
         * Sets the closing brace line
         *
         * @param closingBraceLine the closing brace
         * @return this builder, for chaining
         * @see Line#getClosingBrace()
         */
        public Builder closingBrace(Integer closingBraceLine) {
            this.closingBraceLine = closingBraceLine;
            return this;
        }

        /**
         * Gets the closing brace line
         *
         * @return the closing brace line
         */
        public Integer getClosingBraceLine() {
            return closingBraceLine;
        }

        /**
         * Sets the internal {@link Block}
         *
         * @param internalBlock the internal block
         * @return this builder, for chaining
         * @see Line#getInternalBlock()
         */
        public Builder internal(Block internalBlock) {
            this.internalBlock = internalBlock;
            return this;
        }

        /**
         * Gets the {@link Content} of this line
         *
         * @return the content
         */
        public Content getContent() {
            if (content != null) {
                return content;
            }

            return new Content(line);
        }

        /**
         * Gets the created {@link Line} from this builder
         *
         * @return the line
         */
        public Line getCreated() {
            return created;
        }

        /**
         * Creates a new {@link Line} instance from the builder if not already built
         *
         * @return a new line instance
         */
        public Line build() {
            if (created != null) {
                return created;
            }

            checkNotNull(absoluteLineNumber, "Absolute line number must be specified");
            checkNotNull(scriptLineNumber, "Script line number must be specified");
            checkNotNull(line, "Line must be specified");
            checkNotNull(script, "Script must be specified");
            checkNotNull(blockContainer, "Containing block must be specified");

            return (created = new Line(absoluteLineNumber, scriptLineNumber, getContent(), script, blockContainer, openingBraceLine, closingBraceLine, internalBlock));
        }
    }

    protected static class ContextPossibility { // This class is hidden completely
        private Statement.Arguments argumentSet = null;
        private String[] strargs = null;
        private Map<Statement.Argument, DataContainer> containers = null;

        private Compartment compartment = null;

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

        protected ContextPossibility setStrargs(Collection<String> strargs) {
            this.strargs = strargs.toArray(new String[strargs.size()]);
            return this;
        }

        protected ContextPossibility setStrargs(String[] strargs) {
            this.strargs = strargs;
            return this;
        }

        protected Map<Statement.Argument, DataContainer> getContainers() {
            return containers;
        }

        protected ContextPossibility setContainers(Map<Statement.Argument, DataContainer> containers) {
            this.containers = containers;
            return this;
        }

        protected Compartment getCompartment() {
            return compartment;
        }

        protected ContextPossibility setCompartment(Compartment compartment) {
            this.compartment = compartment;
            return this;
        }
    }
}
