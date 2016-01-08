package com.pqqqqq.directscript.lang.data.container.expression;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.reader.Context;

/**
 * Created by Kevin on 2015-06-17.
 * <p>A {@link ExpressionContainer} that compares two separate {@link DataContainer}s</p>
 * <p>This is slightly different than {@link com.pqqqqq.directscript.lang.data.container.ConditionContainer} in that it does not accept or/and values</p>
 * <p>For external use, usually using the condition container is preferred.</p>
 */
public class ConditionalExpressionContainer extends ExpressionContainer<Boolean> {

    /**
     * Creates a new {@link ConditionalExpressionContainer} with the given {@link DataContainer} terms and the operator
     *
     * @param firstTerm  the first term in the expression (left)
     * @param secondTerm the second term in the expression (right)
     * @param operator   the operator for the expression
     */
    public ConditionalExpressionContainer(DataContainer firstTerm, DataContainer secondTerm, ComparativeOperator operator) {
        super(firstTerm, secondTerm, operator);
    }

    /**
     * Creates a new {@link ConditionalExpressionContainer} with the given {@link DataContainer} terms and the functional operator string
     *
     * @param firstTerm  the first term in the expression (left)
     * @param secondTerm the second term in the expression (right)
     * @param operator   the string operator for the expression
     */
    public ConditionalExpressionContainer(DataContainer firstTerm, DataContainer secondTerm, String operator) {
        this(firstTerm, secondTerm, ComparativeOperator.fromOperator(operator));
    }

    @Override
    public ComparativeOperator getOperator() {
        return (ComparativeOperator) super.getOperator();
    }

    @Override
    public Literal<Boolean> resolve(Context ctx) {
        Literal firstTerm = getFirstTerm().resolve(ctx);
        Literal secondTerm = getSecondTerm().resolve(ctx);
        ComparativeOperator comparator = getOperator();

        switch (comparator) {
            case EQUALS:
                if (firstTerm.isEmpty() && secondTerm.isEmpty()) {
                    return Literal.Literals.TRUE;
                } else if (firstTerm.isEmpty() && !secondTerm.isEmpty() || secondTerm.isEmpty() && !firstTerm.isEmpty()) {
                    return Literal.Literals.FALSE;
                }

                return Literal.fromObject(firstTerm.getValue().equals(secondTerm.getValue()));
            case NOT_EQUALS:
                if (firstTerm.isEmpty() && secondTerm.isEmpty()) {
                    return Literal.Literals.FALSE;
                } else if (firstTerm.isEmpty() && !secondTerm.isEmpty() || secondTerm.isEmpty() && !firstTerm.isEmpty()) {
                    return Literal.Literals.TRUE;
                }

                return Literal.fromObject(!firstTerm.getValue().equals(secondTerm.getValue()));
            case SIMILAR:
                if (firstTerm.isEmpty() && secondTerm.isEmpty()) {
                    return Literal.Literals.TRUE;
                } else if (firstTerm.isEmpty() && !secondTerm.isEmpty() || secondTerm.isEmpty() && !firstTerm.isEmpty()) {
                    return Literal.Literals.FALSE;
                }

                return Literal.fromObject(firstTerm.getString().equalsIgnoreCase(secondTerm.getString()));
            case DISSIMILAR:
                if (firstTerm.isEmpty() && secondTerm.isEmpty()) {
                    return Literal.Literals.FALSE;
                } else if (firstTerm.isEmpty() && !secondTerm.isEmpty() || secondTerm.isEmpty() && !firstTerm.isEmpty()) {
                    return Literal.Literals.TRUE;
                }

                return Literal.fromObject(!firstTerm.getString().equalsIgnoreCase(secondTerm.getString()));
            case LESS_THAN:
                return Literal.fromObject(firstTerm.getNumber() < secondTerm.getNumber());
            case MORE_THAN:
                return Literal.fromObject(firstTerm.getNumber() > secondTerm.getNumber());
            case LESS_THAN_EQUAL_TO:
                return Literal.fromObject(firstTerm.getNumber() <= secondTerm.getNumber());
            case MORE_THAN_EQUAL_TO:
                return Literal.fromObject(firstTerm.getNumber() >= secondTerm.getNumber());
            default:
                throw new IllegalStateException("Unknown comparative operator: " + comparator);
        }
    }

    /**
     * An enumeration of all possible comparative {@link ExpressionOperator}s
     */
    public enum ComparativeOperator implements ExpressionOperator {
        /**
         * Represents a blank operator
         */
        NONE(null),

        /**
         * Represents the equals operator (==)
         */
        EQUALS("=="),

        /**
         * Represents the not equals operator (!=)
         */
        NOT_EQUALS("!="),

        /**
         * Represents the similar operator (~)
         */
        SIMILAR("~"),

        /**
         * Represents the dissimilar operator (!~)
         */
        DISSIMILAR("!~"),

        /**
         * Represents the less than operator (<)
         */
        LESS_THAN("<"),

        /**
         * Represents the more than operator (>)
         */
        MORE_THAN(">"),

        /**
         * Represents the less than of equal to operator (<=)
         */
        LESS_THAN_EQUAL_TO("<="),

        /**
         * Represents the more than equal to operator (>=)
         */
        MORE_THAN_EQUAL_TO(">=");

        private final String operator;

        ComparativeOperator(String operator) {
            this.operator = operator;
        }

        /**
         * Gets the {@link ComparativeOperator} corresponding to the operator string
         *
         * @param operator the functional operator string
         * @return the comparative operator, or {@link #NONE} if none match
         */
        public static ComparativeOperator fromOperator(String operator) {
            for (ComparativeOperator comparativeOperator : values()) {
                if (comparativeOperator.apply(operator)) {
                    return comparativeOperator;
                }
            }

            return NONE;
        }

        /**
         * Gets the functional operator string (eg == for EQUALS)
         *
         * @return the operator string
         */
        public String getOperator() {
            return operator;
        }

        @Override
        public boolean apply(String input) {
            return getOperator() != null && getOperator().equals(input.trim());
        }
    }
}
