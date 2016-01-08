package com.pqqqqq.directscript.lang.statement.sponge.getters;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.util.Utilities;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Collection;

/**
 * Created by Kevin on 2015-12-28.
 * The server getter
 */
public class ServerStatement extends Statement<Object> {
    public static final Syntax SYNTAX = Syntax.builder()
            .identifiers("server")
            .prefix("@")
            .build();

    public ServerStatement() {
        super();

        final Server server = DirectScript.instance().getGame().getServer();
        final Arguments[] GETTER_ARGUMENTS = GenericArguments.getterArguments((Argument) null);

        register(this.<Object, Collection<Player>>createCompartment(new String[]{"players", "allplayers", "online"}, (ctx, arg) -> {
            return Result.<Collection<Player>>builder().success().result(server.getOnlinePlayers()).build();
        }, GETTER_ARGUMENTS));

        register(this.<Object, Collection<World>>createCompartment(new String[]{"worlds", "allworlds"}, (ctx, arg) -> {
            return Result.<Collection<World>>builder().success().result(server.getWorlds()).build();
        }, GETTER_ARGUMENTS));

        register(this.<Object, Text>createCompartment("motd", (ctx, arg) -> {
            return Result.<Text>builder().success().result(server.getMotd()).build();
        }, GETTER_ARGUMENTS));

        register(this.<Object, Boolean>createCompartment("onlinemode", (ctx, arg) -> {
            return Result.<Boolean>builder().success().result(server.getOnlineMode()).build();
        }, GETTER_ARGUMENTS));

        register(this.createCompartment(new String[]{"broadcast", "bc"}, (ctx, arg) -> {
            server.getBroadcastChannel().send(Utilities.getText(ctx.getLiteral("Message").getString()));
            return Result.success();
        }, GenericArguments.requiredArguments((Argument) null, GenericArguments.withName("Message"))));
    }

    @Override
    public Syntax getSyntax() {
        return SYNTAX;
    }
}
