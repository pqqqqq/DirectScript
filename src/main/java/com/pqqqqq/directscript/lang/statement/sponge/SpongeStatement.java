package com.pqqqqq.directscript.lang.statement.sponge;

import com.pqqqqq.directscript.lang.statement.Statement;

/**
 * Created by Kevin on 2015-06-12.
 * A sponge {@link Statement} that has the '@' prefix
 */
public abstract class SpongeStatement<T> extends Statement<T> {

    @Override
    public String getPrefix() {
        return "@";
    }
}
