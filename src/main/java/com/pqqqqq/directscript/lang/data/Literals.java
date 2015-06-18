package com.pqqqqq.directscript.lang.data;

/**
 * Created by Kevin on 2015-06-18.
 * A class of common {@link Literal}s
 */
public class Literals {
    /**
     * An empty {@link Literal}, where the value is absent
     */
    public static final Literal EMPTY = new Literal();

    /**
     * A true {@link Literal}, where the value is true (or 1)
     */
    public static final Literal<Boolean> TRUE = new Literal(true);


    /**
     * A false {@link Literal}, where the value is false (or 0)
     */
    public static final Literal<Boolean> FALSE = new Literal(false);

    /**
     * A {@link Literal} where whose value is a number equal to 0
     */
    public static final Literal<Double> ZERO = new Literal(0D);

    /**
     * A {@link Literal} where whose value is a number equal to 1
     */
    public static final Literal<Double> ONE = new Literal(1D);
}
