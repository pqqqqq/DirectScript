package com.pqqqqq.directscript.events;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.variable.Variable;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import org.spongepowered.api.event.message.CommandEvent;

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
        Causes.PLAYER_JOIN.trigger(ScriptInstance.builder().variables(event.getEntity()));
    }

    @Subscribe
    public void command(CommandEvent event) {
        Causes.COMMAND.trigger(ScriptInstance.builder().variables(event.getSource()).event(event)
                .variables(new Variable("SPONGEcommand", Literal.getLiteralBlindly(event.getCommand()), true), new Variable("SPONGEarguments", Literal.getLiteralBlindly(event.getArguments()), true)));
    }
}
