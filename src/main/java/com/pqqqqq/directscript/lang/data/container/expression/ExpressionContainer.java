package com.pqqqqq.directscript.lang.data.container.expression;

import com.google.common.base.Predicate;
import com.pqqqqq.directscript.lang.data.container.DataContainer;

/**
 * Created by Kevin on 2015-06-17.
 * An abstract {@link DataContainer} which does something to two separate data containers based on the operator they are separated by
 */
public abstract class ExpressionContainer<T> implements DataContainer<T> {
    private final DataContainer firstTerm;
    private final DataContainer secondTerm;
    private final ExpressionOperator operator;

    /**
     * Creates a new {@link ExpressionContainer} with the given {@link DataContainer} terms and the operator
     *
     * @param firstTerm  the first term in the expression (left)
     * @param secondTerm the second term in the expression (right)
     * @param operator   the operator for the expression
     */
    public ExpressionContainer(DataContainer firstTerm, DataContainer secondTerm, ExpressionOperator operator) {
        this.firstTerm = firstTerm;
        this.secondTerm = secondTerm;
        this.operator = operator;
    }

    /**
     * Gets the first {@link DataContainer} terms in this expression
     *
     * @return the first data containers
     */
    public DataContainer getFirstTerm() {
        return firstTerm;
    }

    /**
     * Gets the second {@link DataContainer} terms in this expression
     *
     * @return the second data containers
     */
    public DataContainer getSecondTerm() {
        return secondTerm;
    }

    /**
     * Gets the {@link ExpressionOperator} for this expression
     *
     * @return the operator
     */
    public ExpressionOperator getOperator() {
        return operator;
    }

    /**
     * Represents an operator in an expression that inherits from a String {@link Predicate}
     */
    public interface ExpressionOperator extends Predicate<String> {
    }
}
