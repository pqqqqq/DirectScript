package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.IStatement;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.util.StringUtil;
import org.apache.commons.lang3.tuple.Triple;

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

    public static Sequencer instance(ScriptInstance scriptInstance) {
        return new Sequencer(scriptInstance);
    }

    public Literal parse(@Nonnull String sequence) {
        // TODO: Tidying up and some stray functionality
        checkNotNull(sequence, "Sequence cannot be null");
        Literal result = Literal.empty();

        List<Triple<String, String, String>> triples = StringUtil.splitPairNoQuotes(sequence, "+", "-", "*", "/"); // Split into ordered triple segments
        for (Triple<String, String, String> triple : triples) {
            String beforeSplit = triple.getLeft();
            String segment = triple.getMiddle().trim();
            String afterSplit = triple.getRight();

            Literal segmentLiteral = Literal.empty();

            // Check plain data
            Optional<Literal> literal = Literal.getLiteral(segment);
            if (literal.isPresent()) {
                segmentLiteral = literal.get();
            } else {
                // Check if it's a condition
                literal = conditionInstance.parse(segment);
                if (literal.isPresent()) {
                    segmentLiteral = literal.get();
                } else {
                    if (segment.startsWith("$")) { // Check variable
                        Optional<Variable> variable = scriptInstance.getVariable(segment);
                        if (!variable.isPresent()) {
                            throw new IllegalArgumentException("Invalid variable: " + segment + " in " + sequence);
                        }

                        segmentLiteral = variable.get().getData();
                    } else {
                        // Check if it's a statement
                        Line curLine = scriptInstance.getCurrentLine().get();
                        Line line = new Line(curLine.getAbsoluteNumber(), curLine.getScriptNumber(), segment);
                        Optional<IStatement> statement = Statements.getIStatement(line);
                        if (statement.isPresent()) {
                            StatementResult<?> statementResult = statement.get().run(scriptInstance, line);
                            if (statementResult.getLiteralResult().isPresent()) {
                                segmentLiteral = statementResult.getLiteralResult().get();
                            }
                        }
                    }
                }
            }

            if (segmentLiteral.isEmpty()) {
                throw new IllegalStateException("No coherent sequence could be created from: " + segment + " in: " + sequence); // If all else fails, throw an exception
            }

            if (beforeSplit == null) { // Operators
                result = segmentLiteral;
            } else if (beforeSplit.equals("+")) {
                result = result.add(segmentLiteral);
            } else if (beforeSplit.equals("-")) {
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

        public Optional<Literal> parse(String sequence) {
            String[] splitOr = StringUtil.splitNotInQuotes(sequence, " or "); // 'Or' takes precedence over 'and'

            for (String orCondition : splitOr) {
                String[] splitAnd = StringUtil.splitNotInQuotes(orCondition, " and ");
                int andSuccessCounter = 0;

                for (String condition : splitAnd) {
                    List<Triple<String, String, String>> triples = StringUtil.splitPairNoQuotes(condition, "==", "!=", " < ", " > ", "<=", ">="); // TODO: Better way of less than and more than??

                    if (triples.size() != 2) {
                        return Optional.absent(); // Need exactly a left side and a right side
                    }

                    Triple<String, String, String> leftSideTriple = triples.get(0);
                    Triple<String, String, String> rightSideTriple = triples.get(1);
                    String comparator = leftSideTriple.getRight();

                    if (!comparator.equals(rightSideTriple.getLeft())) { // This should never happen, but it's just an insurance check
                        return Optional.absent();
                    }

                    String leftSide = leftSideTriple.getMiddle();
                    String rightSide = rightSideTriple.getMiddle();

                    if (leftSide == null || rightSide == null) { // Quick null check
                        return Optional.absent();
                    }

                    // Get literals for these values
                    Literal leftSideLiteral = Sequencer.this.parse(leftSide);
                    Literal rightSideLiteral = Sequencer.this.parse(rightSide);

                    // Ensure they're not empty
                    if (leftSideLiteral.isEmpty() || rightSideLiteral.isEmpty()) {
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

                if (andSuccessCounter == splitAnd.length) { // Successful and sequence, the condition is true!
                    return Optional.of(Literal.trueLiteral());
                }
            }

            return Optional.of(Literal.falseLiteral());
        }
    }
}
