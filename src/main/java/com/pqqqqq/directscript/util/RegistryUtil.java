package com.pqqqqq.directscript.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 */
public class RegistryUtil {

    public static <T> List<T> getAllOf(Class<T> type, Class<?> container) {
        List<T> list = new ArrayList<T>();

        for (Field field : container.getFields()) {
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
