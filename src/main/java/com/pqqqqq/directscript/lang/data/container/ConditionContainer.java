package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.Literals;
import com.pqqqqq.directscript.lang.data.container.expression.ConditionalExpressionContainer;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

import java.util.List;

/**
 * Created by Kevin on 2015-06-17.
 * A {@link DataContainer} that contains a two dimensional {@link com.pqqqqq.directscript.lang.data.container.expression.ConditionalExpressionContainer} array of or and and expressions
 */
public class ConditionContainer implements DataContainer<Boolean> {
    private final ConditionalExpressionContainer[][] conditionExpressions;

    /**
     * Creates a new {@link ConditionContainer} with the two-dimensional {@link ConditionalExpressionContainer} array
     *
     * @param conditionExpressions the 2D array
     */
    public ConditionContainer(ConditionalExpressionContainer[]... conditionExpressions) {
        this.conditionExpressions = conditionExpressions;
    }

    /**
     * Creates a new {@link ConditionContainer} with a double-decker condition expression {@link List}
     *
     * @param collection the double collection
     */
    public ConditionContainer(List<? extends List<? extends ConditionalExpressionContainer>> collection) {
        ConditionalExpressionContainer[][] conditionExpressions = new ConditionalExpressionContainer[collection.size()][];
        for (int i = 0; i < collection.size(); i++) {
            List<? extends ConditionalExpressionContainer> row = collection.get(i);
            conditionExpressions[i] = row.toArray(new ConditionalExpressionContainer[row.size()]);
        }

        this.conditionExpressions = conditionExpressions;
    }

    /**
     * <p>Gets the two dimensional {@link ConditionalExpressionContainer} array</p>
     * <p>The first dimension represents OR expressions</p>
     * <p>The second dimension represents AND expressions</p>
     *
     * @return the array
     */
    public ConditionalExpressionContainer[][] getConditionExpressions() {
        return conditionExpressions;
    }

    public Literal<Boolean> resolve(ScriptInstance scriptInstance) {
        andArray:
        for (ConditionalExpressionContainer[] andArray : getConditionExpressions()) {
            if (andArray.length == 0) {
                continue; // Empty arrays don't make it true, they make it false
            }

            for (ConditionalExpressionContainer expression : andArray) {
                if (!expression.resolve(scriptInstance).getBoolean()) {
                    continue andArray; // Expression was unsuccessful, move on to the next array
                }
            }

            return Literals.TRUE; // The only time it should ever reach here is if all expressions are true!
        }

        return Literals.FALSE;
    }
}
