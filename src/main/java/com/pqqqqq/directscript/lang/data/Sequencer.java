package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.StringParser;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 * A sequencer parses values that are dependent on an environment and/or the variables within it. It
 * may also parse a combination of literals. They are parsed to {@link Literal}s
 */
public class Sequencer {
    private ScriptInstance scriptInstance;
    private Condition conditionInstance = new Condition();

    private Sequencer(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    /**
     * Gets a {@link Sequencer} instance for the specific {@link ScriptInstance}
     *
     * @param scriptInstance the script instance
     * @return a new sequencer instance
     */
    public static Sequencer instance(ScriptInstance scriptInstance) {
        return new Sequencer(scriptInstance);
    }

    /**
     * Parses a sequence into a {@link Literal}
     *
     * @param sequence the sequence to parse
     * @return the new literal
     */
    public Literal parse(@Nonnull String sequence) {
        // TODO: Tidying up and some stray functionality
        checkNotNull(sequence, "Sequence cannot be null");

        if (sequence.trim().isEmpty()) {
            return Literal.empty();
        }

        // Check if it's a statement from the get-go
        Optional<Line> curLine = scriptInstance.getCurrentLine();
        if (curLine.isPresent()) {
            Line line = new Line(curLine.get().getAbsoluteNumber(), curLine.get().getScriptNumber(), sequence.trim(), false);
            if (line.getStatement() != null) {
                Statement.Result<?> statementResult = line.toContex(scriptInstance).run();
                if (statementResult.getLiteralResult().isPresent()) {
                    return statementResult.getLiteralResult().get();
                }
            }
        }

        // Check if it's an inline if
        int questionMark = StringParser.instance().indexOf(sequence, "?");
        int colon = StringParser.instance().indexOf(sequence, ":");
        if (questionMark > -1 && colon > -1) {
            String operandCondition = sequence.substring(0, questionMark).trim();
            String trueSegment = sequence.substring(questionMark + 1, colon).trim();
            String falseSegment = sequence.substring(colon + 1).trim();

            if (parse(operandCondition).getBoolean()) {
                return parse(trueSegment);
            } else {
                return parse(falseSegment);
            }
        }

        // Check if it's a condition
        Optional<Literal<Boolean>> conditionLiteral = conditionInstance.parse(sequence);
        if (conditionLiteral.isPresent()) {
            return conditionLiteral.get();
        }

        Literal result = Literal.empty();
        List<StringParser.SplitSequence> triples = StringParser.instance().parseSplitSeq(sequence, "+", " -  ", "*", "/"); // Split into ordered triple segments
        for (StringParser.SplitSequence triple : triples) {
            String beforeSplit = triple.getLeft();
            String segment = triple.getMiddle().trim();
            String afterSplit = triple.getRight();

            Literal segmentLiteral = Literal.empty();
            boolean successful = false;

            // Pre-parse anything in brackets
            String bracket;
            while ((bracket = StringParser.instance().getOuterBracket(segment, '(', ')')) != null) {
                segment = segment.replace(bracket, parse(bracket.substring(1, bracket.length() - 1)).normalize().getString()).trim(); // Normalize brackets since they're being put back in
            }

            boolean negative = false; // Check preceding exclamation points (negation)
            while (segment.startsWith("!")) {
                negative = !negative;
                segment = segment.substring(1);
            }

            // Check plain data
            Optional<Literal> literal = Literal.getLiteral(scriptInstance, segment);
            if (literal.isPresent()) {
                segmentLiteral = literal.get();
                successful = true;
            } else {
                // Check variable
                Optional<Variable> variable = scriptInstance.getEnvironment().getVariable(segment);
                if (variable.isPresent()) {
                    segmentLiteral = variable.get().getData();
                    successful = true;
                }
            }

            if (!successful) { // Ensure a segment was found
                throw new IllegalStateException("No coherent segment could be created from: '" + segment + "' in the sequence: '" + sequence + "'"); // If all else fails, throw an exception
            }

            if (negative) {
                segmentLiteral = segmentLiteral.negative(); // Negate if necessary
            }

            if (beforeSplit == null) { // Operators
                result = segmentLiteral;
            } else if (beforeSplit.equals("+")) {
                result = result.add(segmentLiteral);
            } else if (beforeSplit.equals(" - ")) {
                result = result.sub(segmentLiteral);
            } else if (beforeSplit.equals("*")) {
                result = result.mult(segmentLiteral);
            } else if (beforeSplit.equals("/")) {
                result = result.div(segmentLiteral);
            } else {
                throw new IllegalStateException("Unknown operator " + beforeSplit + " in " + sequence);
            }
        }

        return result;
    }

    class Condition {
        private Condition() {
        }

        Optional<Literal<Boolean>> parse(String sequence) {
            String[] splitOr = StringParser.instance().parseSplit(sequence, " or "); // 'Or' takes precedence over 'and'

            for (String orCondition : splitOr) {
                String[] splitAnd = StringParser.instance().parseSplit(orCondition, " and ");
                int andSuccessCounter = 0;

                for (String condition : splitAnd) {
                    List<StringParser.SplitSequence> triples = StringParser.instance().parseSplitSeq(condition, "==", "!=", "~", "!~", " < ", " > ", "<=", ">="); // TODO: Better way of less than and more than??

                    if (triples.size() != 2) {
                        return Optional.absent(); // Need exactly a left side and a right side
                    }

                    StringParser.SplitSequence leftSideTriple = triples.get(0);
                    StringParser.SplitSequence rightSideTriple = triples.get(1);
                    String comparator = leftSideTriple.getAfterSplit();

                    if (!comparator.equals(rightSideTriple.getBeforeSplit())) { // This should never happen, but it's just an insurance check
                        return Optional.absent();
                    }

                    String leftSide = leftSideTriple.getSequence();
                    String rightSide = rightSideTriple.getSequence();

                    if (leftSide == null || rightSide == null) { // Quick null check
                        return Optional.absent();
                    }

                    // Get literals for these values
                    Literal leftSideLiteral = Sequencer.this.parse(leftSide);
                    Literal rightSideLiteral = Sequencer.this.parse(rightSide);

                    // Ensure they're not null
                    if (leftSideLiteral == null || rightSideLiteral == null) {
                        return Optional.absent();
                    }

                    // Do check
                    if (comparator.equals("==")) { // Equals
                        if (leftSideLiteral.getValue().equals(rightSideLiteral.getValue())) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals("!=")) { // Not equals
                        if (!leftSideLiteral.getValue().equals(rightSideLiteral.getValue())) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals("~")) { // Similar
                        if (leftSideLiteral.getString().equalsIgnoreCase(rightSideLiteral.getString())) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals("!~")) { // Not similar
                        if (!leftSideLiteral.getString().equalsIgnoreCase(rightSideLiteral.getString())) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals(" < ")) { // Less than
                        if (leftSideLiteral.getNumber() < rightSideLiteral.getNumber()) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals(" > ")) { // More than
                        if (leftSideLiteral.getNumber() > rightSideLiteral.getNumber()) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals("<=")) { // Less than or equal to
                        if (leftSideLiteral.getNumber() <= rightSideLiteral.getNumber()) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals(">=")) { // More than or equal to
                        if (leftSideLiteral.getNumber() >= rightSideLiteral.getNumber()) {
                            andSuccessCounter++;
                        }
                    } else { // Unknown
                        return Optional.absent();
                    }
                }

                if (andSuccessCounter == splitAnd.length) { // Successful 'and' sequence, the condition is true!
                    return Optional.of(Literal.trueLiteral());
                }
            }

            return Optional.of(Literal.falseLiteral());
        }
    }
}
