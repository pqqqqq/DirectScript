package com.pqqqqq.directscript.lang.statement;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.util.Utilities;

/**
 * Created by Kevin on 2015-12-29.
 * <p>A {@link Statement} compartment; a section of a statement.</p>
 * <p>All statements have at least one compartment.</p>
 */
public interface Compartment<T, R> extends Context.Runnable.Argumentative<T, R> {

    /**
     * Gets the {@link Statement.Arguments} array
     *
     * @return the arguments array
     */
    Statement.Arguments[] getArgumentsArray();

    String[] getGetters();

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

    default void register(Statement statement) {
        statement.register(this);
    }
}
