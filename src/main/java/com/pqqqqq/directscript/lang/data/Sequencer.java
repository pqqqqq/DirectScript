package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.container.*;
import com.pqqqqq.directscript.lang.data.container.expression.ArithmeticContainer;
import com.pqqqqq.directscript.lang.data.container.expression.ConditionalExpressionContainer;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringParser;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 * A sequencer parses values that are dependent on an environment and/or the variables within it. It
 * may also parse a combination of literals. They are parsed to {@link Literal}s
 */
public class Sequencer {
    private static final String[][] LITERAL_DELIMITER_GROUPS = {{" + ", " - "}, {"*", "/"}, {"^", "`"}}; // The +/- group is first since we want these split first, not last
    private static final String[][] CONDITION_DELIMITER_GROUPS = {{"==", "!=", "~", "!~", "<=", ">="}, {"<", ">"}}; // Each in the same split group because equal priority. < and > in separate because <= and >= check first
    private static final Sequencer INSTANCE = new Sequencer();
    private final Condition conditionInstance = new Condition();

    private Sequencer() {
    }

    /**
     * Gets the {@link Sequencer} instance
     *
     * @return the sequencer instance
     */
    public static Sequencer instance() {
        return INSTANCE;
    }

    /**
     * Parses a sequence into a {@link DataContainer}
     *
     * @param sequence the sequence to parse
     * @return the new data container
     */
    public DataContainer parse(String sequence) {
        checkNotNull(sequence, "Sequence cannot be null");

        sequence = sequence.trim();
        if (sequence.isEmpty()) {
            return Literal.empty();
        }

        // Check preceding exclamation points (negation)
        String negateTrimSequence = sequence;
        boolean negative = false;
        while (negateTrimSequence.startsWith("!")) {
            negative = !negative;
            negateTrimSequence = negateTrimSequence.substring(1);
        }

        /* Brackets, ternary operators and conditions in general must not be split into operators, since they can have operators within their conditions */
        // Pre-parse anything in brackets. Use negateTrimmedSequence
        if (negateTrimSequence.startsWith("(") && negateTrimSequence.endsWith(")")) {
            return negateIfNecessary(parse(negateTrimSequence.substring(1, negateTrimSequence.length() - 1)), negative);
        }

        // Check if it's a ternary operator. Don't use negateTrimmedSequence
        int questionMark = StringParser.instance().indexOf(sequence, "?");
        int colon = StringParser.instance().indexOf(sequence, ":");
        if (questionMark > -1 && colon > -1) {
            DataContainer conditionContainer = parse(sequence.substring(0, questionMark).trim());
            DataContainer trueContainer = parse(sequence.substring(questionMark + 1, colon).trim());
            DataContainer falseContainer = parse(sequence.substring(colon + 1).trim());

            return new TernaryOperatorContainer(conditionContainer, trueContainer, falseContainer);
        }

        // Check if it's a condition. Don't use negateTrimmedSequence
        Optional<DataContainer<Boolean>> conditionLiteral = conditionInstance.parse(sequence);
        if (conditionLiteral.isPresent()) {
            return conditionLiteral.get();
        }

        StringParser.SplitSequence triple = StringParser.instance().parseNextSequence(sequence, LITERAL_DELIMITER_GROUPS); // Split into ordered triple segments
        if (triple == null || triple.getDelimiter() == null) { // Check if there's no split string
            // Check if it's a statement. Don't use negateTrimmedSequence
            if (Statements.getStatement(sequence).isPresent()) {
                return new StatementContainer(sequence);
            }

            // Check if it's an array
            if (negateTrimSequence.startsWith("[") && negateTrimSequence.endsWith("]")) {
                List<DataContainer> array = new ArrayList<DataContainer>();

                int index = 0;
                for (String arrayValue : StringParser.instance().parseSplit(negateTrimSequence.substring(1, negateTrimSequence.length() - 1), ",")) {
                    array.add(parse(arrayValue));
                }
                return new ListContainer(array);
            }

            // Check plain data. Use negativeTrimSequence
            Optional<Literal> literal = Literal.getLiteral(negateTrimSequence);
            if (literal.isPresent()) {
                return negateIfNecessary(literal.get(), negative);
            }

            // Worst comes to worst, assume its a variable container (use negativeTrimSequence)
            return negateIfNecessary(new VariableContainer(negateTrimSequence), negative);
        }

        return new ArithmeticContainer(parse(triple.getBeforeSegment()), parse(triple.getAfterSegment()), ArithmeticContainer.ArithmeticOperator.fromOperator(triple.getDelimiter()));
    }

    private DataContainer negateIfNecessary(DataContainer dataContainer, boolean negate) {
        return negate ? new NegateContainer(dataContainer) : dataContainer;
    }

    class Condition {
        private Condition() {
        }

        Optional<DataContainer<Boolean>> parse(String sequence) {
            String[] splitOr = StringParser.instance().parseSplit(sequence, " or "); // 'Or' takes precedence over 'and'
            List<List<ConditionalExpressionContainer>> mainExpressionList = new ArrayList<List<ConditionalExpressionContainer>>();

            for (String orCondition : splitOr) {
                String[] splitAnd = StringParser.instance().parseSplit(orCondition, " and ");
                List<ConditionalExpressionContainer> andExpressionList = new ArrayList<ConditionalExpressionContainer>();

                for (String condition : splitAnd) {
                    StringParser.SplitSequence triple = StringParser.instance().parseNextSequence(condition, CONDITION_DELIMITER_GROUPS);

                    if (triple == null || triple.getDelimiter() == null) {
                        return Optional.absent(); // Need exactly a left side and a right side
                    }

                    // Get literals for these values
                    DataContainer leftSideLiteral = Sequencer.this.parse(triple.getBeforeSegment());
                    DataContainer rightSideLiteral = Sequencer.this.parse(triple.getAfterSegment());
                    String comparator = triple.getDelimiter();

                    andExpressionList.add(new ConditionalExpressionContainer(leftSideLiteral, rightSideLiteral, ConditionalExpressionContainer.ComparativeOperator.fromOperator(comparator)));
                }

                mainExpressionList.add(andExpressionList);
            }

            return Optional.<DataContainer<Boolean>>of(new ConditionContainer(mainExpressionList));
        }
    }
}
