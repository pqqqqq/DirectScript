package com.pqqqqq.directscript.lang.data.container.expression;

import com.pqqqqq.directscript.lang.data.Data;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.container.DataContainer;
import com.pqqqqq.directscript.lang.reader.Context;


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

    @Override
    public Datum resolve(Context ctx) {
        Datum firstDatum = getFirstTerm().resolve(ctx);
        Datum secondDatum = getSecondTerm().resolve(ctx);

        ArithmeticOperator operator = getOperator();

        switch (operator) {
            case ADDITION:
                return new Data(firstDatum, secondDatum);
            case SUBTRACTION:
                return firstDatum.get().sub(secondDatum.get());
            case MULTIPLICATION:
                return firstDatum.get().mult(secondDatum.get());
            case DIVISION:
                return firstDatum.get().div(secondDatum.get());
            case EXPONENTIAL:
                return firstDatum.get().pow(secondDatum.get());
            case ROOT:
                return firstDatum.get().root(secondDatum.get());
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

        @Override
        public boolean apply(String input) {
            return getOperator() != null && getOperator().equals(input.trim());
        }
    }
}
