package com.pqqqqq.directscript.events;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created by Kevin on 2015-06-03.
 */
public class TriggerEvents {
    private DirectScript plugin;

    public TriggerEvents(DirectScript plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void join(PlayerJoinEvent event) {
        Causes.PLAYER_JOIN.trigger(ScriptInstance.builder()
                .causedBy(event.getEntity()));
    }

    @Subscribe
    public void command(CommandEvent event) {
        CommandSource source = event.getSource();
        Causes.COMMAND.trigger(ScriptInstance.builder()
                .event(event)
                .causedBy((source instanceof Player ? (Player) source : null))
                .variables(source)
                .variables(new Variable("sponge.command", Literal.getLiteralBlindly(event.getCommand()), true), new Variable("sponge.arguments", Literal.getLiteralBlindly(event.getArguments()), true)));
    }
}
