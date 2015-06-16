package com.pqqqqq.directscript.lang.data;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.data.env.Variable;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.StringParser;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-02.
 * A sequencer parses values that are dependent on an environment and/or the variables within it. It
 * may also parse a combination of literals. They are parsed to {@link Literal}s
 */
public class Sequencer {
    private static final String[][] LITERAL_SPLIT_GROUPS = {{" + ", " - "}, {"*", "/"}, {"^", "`"}}; // The +/- group is first since we want these split first, not last
    private static final String[][] CONDITION_SPLIT_GROUPS = {{"==", "!=", "~", "!~", "<=", ">="}, {"<", ">"}}; // Each in the same split group because equal priority. < and > in separate because <= and >= check first
    private final ScriptInstance scriptInstance;
    private final Condition conditionInstance = new Condition();

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
    public Literal parse(String sequence) {
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

        /* Brackets, inline ifs and conditions in general must not be split into operators, since they can have operators within their conditions */
        // Pre-parse anything in brackets. Use negateTrimmedSequence
        if (negateTrimSequence.startsWith("(") && negateTrimSequence.endsWith(")")) {
            return negateIfNecessary(parse(negateTrimSequence.substring(1, negateTrimSequence.length() - 1)), negative);
        }

        // Check if it's an inline if. Don't use negateTrimmedSequence
        int questionMark = StringParser.instance().indexOf(sequence, "?");
        int colon = StringParser.instance().indexOf(sequence, ":");
        if (questionMark > -1 && colon > -1) {
            String operandCondition = sequence.substring(0, questionMark).trim();
            String trueSegment = sequence.substring(questionMark + 1, colon).trim();
            String falseSegment = sequence.substring(colon + 1).trim();

            return parse(operandCondition).getBoolean() ? parse(trueSegment) : parse(falseSegment); // Use an inline if for the inline if... inception
        }

        // Check if it's a condition. Don't use negateTrimmedSequence
        Optional<Literal<Boolean>> conditionLiteral = conditionInstance.parse(sequence);
        if (conditionLiteral.isPresent()) {
            return conditionLiteral.get();
        }

        StringParser.SplitSequence triple = StringParser.instance().parseNextSequence(sequence, LITERAL_SPLIT_GROUPS); // Split into ordered triple segments
        if (triple == null || triple.getSplit() == null) { // Check if there's no split string
            // Check if it's a statement. Don't use negateTrimmedSequence
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

            // Check plain data. Use negativeTrimSequence
            Optional<Literal> literal = Literal.getLiteral(scriptInstance, negateTrimSequence);
            if (literal.isPresent()) {
                return negateIfNecessary(literal.get(), negative);
            }

            // Check variable. Use negativeTrimSequence
            Optional<Variable> variable = scriptInstance.getEnvironment().getVariable(negateTrimSequence);
            if (variable.isPresent()) {
                return negateIfNecessary(variable.get().getData(), negative);
            }

            throw new IllegalStateException("No coherent segment could be created from: '" + sequence + "'"); // If all else fails, throw an exception
        }

        Literal before = parse(triple.getBeforeSegment());
        Literal after = parse(triple.getAfterSegment());
        String split = triple.getSplit();

        if (split.equals(" + ")) { // Addition
            return before.add(after);
        }
        if (split.equals(" - ")) { // Subtraction
            return before.sub(after);
        }
        if (split.equals("*")) { // Multiplication
            return before.mult(after);
        }
        if (split.equals("/")) { // Division
            return before.div(after);
        }
        if (split.equals("^")) { // Exponent
            return before.pow(after);
        }
        if (split.equals("`")) { // Root
            return before.root(after);
        }

        throw new IllegalStateException("Unknown operator " + split + " in " + sequence);
    }

    private Literal negateIfNecessary(Literal literal, boolean negate) {
        return negate ? literal.negative() : literal;
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
                    StringParser.SplitSequence triple = StringParser.instance().parseNextSequence(condition, CONDITION_SPLIT_GROUPS);

                    if (triple == null || triple.getSplit() == null) {
                        return Optional.absent(); // Need exactly a left side and a right side
                    }

                    // Get literals for these values
                    Literal leftSideLiteral = Sequencer.this.parse(triple.getBeforeSegment());
                    Literal rightSideLiteral = Sequencer.this.parse(triple.getAfterSegment());
                    String comparator = triple.getSplit();

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
                    } else if (comparator.equals("<")) { // Less than
                        if (leftSideLiteral.getNumber() < rightSideLiteral.getNumber()) {
                            andSuccessCounter++;
                        }
                    } else if (comparator.equals(">")) { // More than
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
