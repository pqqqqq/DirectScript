package com.pqqqqq.directscript.lang.data.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-13.
 * An converter which has {@link Function}s of conversions embedded.
 */
public class Converter<T> {
    protected final Map<Class, Function<T, ?>> conversions;
    private final Class<T> genericClass;
    private final Converter[] parents;

    public Converter(Class<T> genericClass, Map<Class, Function<T, ?>> conversions) {
        this(genericClass, conversions, new Converter[0]);
    }

    public Converter(Class<T> genericClass, Converter<? super T>... parents) {
        this(genericClass, new HashMap<>(), parents);
    }

    public Converter(Class<T> genericClass, Map<Class, Function<T, ?>> conversions, Converter<? super T>... parents) {
        this.genericClass = genericClass;
        this.parents = parents;

        this.conversions = new HashMap<>();
        this.conversions.put(String.class, Object::toString); // Basic toString that can be overriden

        this.updateParents();
        this.conversions.putAll(conversions); // Necessary this way so higher level entries are kept
    }

    /**
     * Converts this {@link Converter} as defined by a {@link Function} between an instance and a type
     *
     * @param instance the instance of this abstract objective
     * @param type     the type to convert to
     * @return the converted instance as generic type R
     */
    public <R> Optional<R> convert(T instance, Class<R> type) {
        if (conversions.containsKey(type)) {
            return (Optional<R>) Optional.of(conversions.get(type).apply(instance));
        }

        if (type.isInstance(instance)) {
            return (Optional<R>) Optional.of(instance); // Attempt to cast directly. Done like this because the asm injections sponge uses blocks the use of checking the classes directly.
        }

        return Optional.empty();
    }

    /**
     * Checks whether this type is eligible to be converted
     *
     * @return true if eligible
     */
    public boolean isEligible(Object object) {
        return getGenericClass().isInstance(object);
    }

    /**
     * Gets the name of the generic class
     *
     * @return the name
     */
    public String getName() {
        return getGenericClass().getSimpleName();
    }

    /**
     * Gets the generic {@link Class} that this converter can convert for
     *
     * @return the generic class
     */
    public Class<T> getGenericClass() {
        return genericClass;
    }

    protected <U> void addInheritance(final Converter<U> parent, final Function<T, U> function) {
        parent.conversions.entrySet().forEach((entry) -> {
            this.conversions.putIfAbsent(entry.getKey(), (T input) -> entry.getValue().apply(function.apply(input)));
        });
        this.conversions.put(parent.getGenericClass(), function); // Always should be this
    }

    private void updateParents() {
        Arrays.asList(this.parents).forEach((parent) -> {
            this.conversions.putAll(parent.conversions);
        });
    }
}
