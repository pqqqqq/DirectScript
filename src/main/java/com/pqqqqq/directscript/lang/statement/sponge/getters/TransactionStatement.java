package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.Transaction;

import java.util.Optional;

/**
 * Created by Kevin on 2015-11-15.
 * A statement for different {@link Transaction}s
 */
public class TransactionStatement extends Statement<DataSerializable> {

    public TransactionStatement() {
        super(Syntax.builder()
                .identifiers("transaction")
                .prefix("@")
                .arguments(Arguments.of(Argument.from("Getter")), Arguments.of(Argument.from("Transaction"), ",", Argument.from("Getter")))
                .arguments(Arguments.of(Argument.from("Transaction"), ",", Argument.from("Getter"), ",", Argument.from("Arguments", Argument.REST_AS_LIST)))
                .build());
    }

    @Override
    public Result<DataSerializable> run(Context ctx) {
        Optional<Transaction<DataSerializable>> transaction = ctx.getLiteral("Transaction", Transaction.class).getAs(Transaction.class);
        if (!transaction.isPresent()) {
            return Result.failure();
        }

        String getter = ctx.getLiteral("Getter").getString();
        switch (getter.toLowerCase()) {
            case "before":
                return Result.<DataSerializable>builder().success().result(transaction.get().getOriginal()).build();
            case "after":
                return Result.<DataSerializable>builder().success().result(transaction.get().getFinal()).build();
            case "custom":
                return Result.<DataSerializable>builder().success().result(transaction.get().getCustom().orElse(null)).build();
            case "default":
                return Result.<DataSerializable>builder().success().result(transaction.get().getDefault()).build();
        }

        return Result.failure();
    }
}
