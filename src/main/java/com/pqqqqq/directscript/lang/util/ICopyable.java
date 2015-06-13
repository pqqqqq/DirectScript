package com.pqqqqq.directscript.lang.util;

/**
 * Created by Kevin on 2015-06-08.
 * Represents an interface that can be copied
 */
public interface ICopyable<T> {

    /**
     * Copies the instance
     *
     * @return the copy
     */
    T copy();
}
