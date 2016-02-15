package com.pqqqqq.directscript.lang.statement;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.util.Utilities;

/**
 * Created by Kevin on 2015-12-29.
 * <p>A {@link Statement} compartment - a section of a statement.</p>
 * <p>Statements themselves are compartments, and their default run has a null argument in {@link com.pqqqqq.directscript.lang.reader.Context.Runnable.Argumentative#run(Context, Object)}</p>
 */
public interface Compartment<T, R> extends Context.Runnable.Argumentative<T, R> {

    /**
     * Gets the {@link Statement.Arguments} array
     *
     * @return the arguments array
     */
    Statement.Arguments[] getArgumentsArray();

    /**
     * Gets the array of getters
     *
     * @return the getter
     */
    String[] getGetters();

    /**
     * <p>Gets if this compartment has the given getter.</p>
     *
     * <p>This method is preferred to be used over checking by iterating through {@link #getGetters()} manually, since
     * all non-alphanumeric characters are removed from the given getter ({@link Utilities#removeNonAlphanumeric(String)}), and cases are ignored.</p>
     *
     * @param getter the getter
     * @return true if the compartment has the getter
     */
    default boolean containsGetter(String getter) {
        if (getGetters() == null) {
            return false;
        }

        getter = Utilities.removeNonAlphanumeric(getter);
        for (String item : getGetters()) {
            if (getter.equalsIgnoreCase(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Registers the compartment to the {@link Statement}
     * @param statement the statement
     */
    default void register(Statement statement) {
        statement.register(this);
    }
}
