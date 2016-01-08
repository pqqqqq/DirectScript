package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.Transaction;

import static com.pqqqqq.directscript.lang.statement.Statement.GenericArguments.DEFAULT_TRANSACTION;

/**
 * Created by Kevin on 2015-11-15.
 * A statement for different {@link Transaction}s
 */
public class TransactionStatement extends Statement<DataSerializable> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("transaction")
            .prefix("@")
            .build();

    public TransactionStatement() {
        super();

        final Arguments[] GETTER_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<Transaction, DataSerializable>createCompartment("before", (ctx, transaction) -> {
            return Result.<DataSerializable>builder().success().result(transaction.getOriginal()).build();
        }, GETTER_ARGUMENTS));

        register(this.<Transaction, DataSerializable>createCompartment("after", (ctx, transaction) -> {
            return Result.<DataSerializable>builder().success().result(transaction.getFinal()).build();
        }, GETTER_ARGUMENTS));
    }

    @Override
    public Argument getObjectArgument() {
        return DEFAULT_TRANSACTION;
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
