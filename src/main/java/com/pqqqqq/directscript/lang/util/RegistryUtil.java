package com.pqqqqq.directscript.lang.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 * Represents utilities for registries, such as {@link com.pqqqqq.directscript.lang.trigger.cause.Causes} and {@link com.pqqqqq.directscript.lang.statement.Statements}
 */
public class RegistryUtil {

    /**
     * Gets all of the specific type in a registry
     *
     * @param type     the type class the field must be a subclass of
     * @param registry the registry class
     * @param <T>      the type parameter for the list
     * @return a {@link List} of subclasses of type
     */
    public static <T> List<T> getAllOf(Class<T> type, Class<?> registry) {
        List<T> list = new ArrayList<T>();

        for (Field field : registry.getFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    Object fieldGet = field.get(null);
                    if (type.isInstance(fieldGet)) {
                        list.add((T) fieldGet);
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        return list;
    }
}
