package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.lang.statement.Statement;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.selector.Selector;

import java.util.Set;

/**
 * Created by Kevin on 2016-01-13.
 * A statement for command sources
 */
public class SourceStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("source")
            .prefix("@")
            .build();

    public SourceStatement() {
        super();

        final Arguments[] GETTER_ARGUMENTS = GenericArguments.getterArguments(this);
        register(this.<CommandSource, String>createCompartment("name", (ctx, source) -> {
            return Result.<String>builder().success().result(source.getName()).build();
        }, GETTER_ARGUMENTS));

        register(this.<CommandSource, Boolean>createCompartment(new String[]{"permission", "haspermission", "perm", "hasperm"}, (ctx, source) -> {
            return Result.<Boolean>builder().success().result(source.hasPermission(ctx.getLiteral("Permissiom").getString())).build();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Permission"))));

        register(this.<CommandSource, Set<Entity>>createCompartment("selector", (ctx, source) -> {
            @SuppressWarnings("deprecation") Selector selector = Sponge.getRegistry().getSelectorFactory().parseRawSelector(ctx.getLiteral("Selector").getString());
            return Result.<Set<Entity>>builder().success().result(selector.resolve(source)).build();
        }, GenericArguments.requiredArguments(this, GenericArguments.withName("Selector"))));
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
