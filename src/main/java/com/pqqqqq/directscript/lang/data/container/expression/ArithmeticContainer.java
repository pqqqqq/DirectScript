package com.pqqqqq.directscript.lang.data.container.expression;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

/**
 * Created by Kevin on 2015-06-17.
 * Represents a {@link ExpressionContainer} responsible for arithmetic at runtime
 */
public class ArithmeticContainer extends ExpressionContainer {

    /**
     * Creates a new {@link ArithmeticContainer} with the given {@link DataContainer} terms and the operator
     *
     * @param firstTerm  the first term in the expression (left)
     * @param secondTerm the second term in the expression (right)
     * @param operator   the operator for the expression
     */
    public ArithmeticContainer(DataContainer firstTerm, DataContainer secondTerm, ArithmeticOperator operator) {
        super(firstTerm, secondTerm, operator);
    }

    /**
     * Creates a new {@link ArithmeticContainer} with the given {@link DataContainer} terms and the functional operator string
     *
     * @param firstTerm  the first term in the expression (left)
     * @param secondTerm the second term in the expression (right)
     * @param operator   the string operator for the expression
     */
    public ArithmeticContainer(DataContainer firstTerm, DataContainer secondTerm, String operator) {
        this(firstTerm, secondTerm, ArithmeticOperator.fromOperator(operator));
    }

    @Override
    public ArithmeticOperator getOperator() {
        return (ArithmeticOperator) super.getOperator();
    }

    public Literal resolve(ScriptInstance scriptInstance) {
        Literal firstLiteral = getFirstTerm().resolve(scriptInstance);
        Literal secondLiteral = getSecondTerm().resolve(scriptInstance);
        ArithmeticOperator operator = getOperator();

        switch (operator) {
            case ADDITION:
                return firstLiteral.add(secondLiteral);
            case SUBTRACTION:
                return firstLiteral.sub(secondLiteral);
            case MULTIPLICATION:
                return firstLiteral.mult(secondLiteral);
            case DIVISION:
                return firstLiteral.div(secondLiteral);
            case EXPONENTIAL:
                return firstLiteral.pow(secondLiteral);
            case ROOT:
                return firstLiteral.root(secondLiteral);
            default:
                throw new IllegalStateException("Unknown arithmetic operator: " + operator);
        }
    }

    /**
     * An enumeration of all possible arithmetic {@link ExpressionOperator}s
     */
    public enum ArithmeticOperator implements ExpressionOperator {
        /**
         * Represents a blank operator
         */
        NONE(null),

        /**
         * Represents an addition operator (+)
         */
        ADDITION("+"),

        /**
         * Represents a subtraction operator (-)
         */
        SUBTRACTION("-"),

        /**
         * Represents a multiplication operator (*)
         */
        MULTIPLICATION("*"),

        /**
         * Represents a division operator (/)
         */
        DIVISION("/"),

        /**
         * Represents an exponential operator (^)
         */
        EXPONENTIAL("^"),

        /**
         * Represents a root operator (`)
         */
        ROOT("`");

        private final String operator;

        ArithmeticOperator(String operator) {
            this.operator = operator;
        }

        /**
         * Gets the {@link ArithmeticOperator} corresponding to the operator string
         *
         * @param operator the functional operator string
         * @return the arithmetic operator, or {@link #NONE} if none match
         */
        public static ArithmeticOperator fromOperator(String operator) {
            for (ArithmeticOperator arithmeticOperator : values()) {
                if (arithmeticOperator.apply(operator)) {
                    return arithmeticOperator;
                }
            }

            return NONE;
        }

        /**
         * Gets the functional operator string (eg + for ADDITION)
         *
         * @return the operator string
         */
        public String getOperator() {
            return operator;
        }

        public boolean apply(String input) {
            return getOperator() != null && getOperator().equals(input.trim());
        }
    }
}
