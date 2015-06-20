package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.Lang;
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
            return Literals.EMPTY;
        }

        // Get rid of brackets if they're still there
        if (sequence.startsWith("(") && sequence.endsWith(")")) {
            sequence = sequence.substring(1, sequence.length() - 1);
        }

        // Check if it's a ternary operator
        int questionMark = Lang.instance().stringParser().indexOf(sequence, "?");
        int colon = Lang.instance().stringParser().indexOf(sequence, ":");
        if (questionMark > -1 && colon > -1) {
            DataContainer conditionContainer = parse(sequence.substring(0, questionMark).trim());
            DataContainer trueContainer = parse(sequence.substring(questionMark + 1, colon).trim());
            DataContainer falseContainer = parse(sequence.substring(colon + 1).trim());

            return new TernaryOperatorContainer(conditionContainer, trueContainer, falseContainer);
        }

        // Check if it's a condition
        Optional<DataContainer<Boolean>> conditionLiteral = conditionInstance.parse(sequence);
        if (conditionLiteral.isPresent()) {
            return conditionLiteral.get();
        }

        StringParser.SplitSequence triple = Lang.instance().stringParser().parseNextSequence(sequence, LITERAL_DELIMITER_GROUPS); // Split into ordered triple segments
        if (triple == null || triple.getDelimiter() == null) { // Check if there's no split string
            // Check if it's a statement
            if (Statements.getStatement(sequence).isPresent()) {
                return new StatementContainer(sequence);
            }

            // Check leading exclamation points (negation)
            if (sequence.startsWith("!")) {
                return new NegateContainer(parse(sequence.substring(1)));
            }

            // Check trailing array index values
            if (sequence.endsWith("]")) {
                int index = Lang.instance().stringParser().lastIndexOf(sequence, "[");
                if (index > -1) {
                    return new ArrayIndexContainer(parse(sequence.substring(0, index)), parse(sequence.substring(index + 1, sequence.length() - 1)));
                }
            }

            // Check if it's an array
            if (sequence.startsWith("{") && sequence.endsWith("}")) {
                List<DataContainer> array = new ArrayList<DataContainer>();

                int index = 0;
                for (String arrayValue : Lang.instance().stringParser().parseSplit(sequence.substring(1, sequence.length() - 1), ",")) {
                    array.add(parse(arrayValue));
                }
                return new ArrayContainer(array);
            }

            // Check plain data
            Optional<Literal> literal = Literal.getLiteral(sequence);
            if (literal.isPresent()) {
                return literal.get();
            }

            return new VariableContainer(sequence); // Worst comes to worst, assume its a variable container
        }

        return new ArithmeticContainer(parse(triple.getBeforeSegment()), parse(triple.getAfterSegment()), triple.getDelimiter());
    }

    class Condition {
        private Condition() {
        }

        Optional<DataContainer<Boolean>> parse(String sequence) {
            String[] splitOr = Lang.instance().stringParser().parseSplit(sequence, " or "); // 'Or' takes precedence over 'and'
            List<List<ConditionalExpressionContainer>> mainExpressionList = new ArrayList<List<ConditionalExpressionContainer>>();

            for (String orCondition : splitOr) {
                String[] splitAnd = Lang.instance().stringParser().parseSplit(orCondition, " and ");
                List<ConditionalExpressionContainer> andExpressionList = new ArrayList<ConditionalExpressionContainer>();

                for (String condition : splitAnd) {
                    StringParser.SplitSequence triple = Lang.instance().stringParser().parseNextSequence(condition, CONDITION_DELIMITER_GROUPS);

                    if (triple == null || triple.getDelimiter() == null) {
                        return Optional.absent(); // Need exactly a left side and a right side
                    }

                    // Get literals for these values
                    DataContainer leftSideLiteral = Sequencer.this.parse(triple.getBeforeSegment());
                    DataContainer rightSideLiteral = Sequencer.this.parse(triple.getAfterSegment());
                    String comparator = triple.getDelimiter();

                    andExpressionList.add(new ConditionalExpressionContainer(leftSideLiteral, rightSideLiteral, comparator));
                }

                mainExpressionList.add(andExpressionList);
            }

            return Optional.<DataContainer<Boolean>>of(new ConditionContainer(mainExpressionList));
        }
    }
}
