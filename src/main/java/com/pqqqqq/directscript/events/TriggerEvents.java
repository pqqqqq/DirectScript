package com.pqqqqq.directscript.events;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.*;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.text.Texts;
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
        Causes.PLAYER_JOIN.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity()));
    }

    @Subscribe
    public void quit(PlayerQuitEvent event) {
        Causes.PLAYER_QUIT.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity()));
    }

    @Subscribe
    public void death(PlayerDeathEvent event) {
        Causes.PLAYER_DEATH.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity()));
    }

    @Subscribe
    public void chat(PlayerChatEvent event) {
        Causes.PLAYER_CHAT.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("Message", Texts.toPlain(event.getNewMessage())));
    }

    @Subscribe
    public void itemPickup(PlayerPickUpItemEvent event) {
        Causes.ITEM_PICKUP.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("Items", event.getItems()));
    }

    @Subscribe
    public void itemDrop(PlayerDropItemEvent event) {
        Causes.ITEM_DROP.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("ItemStacks", event.getDroppedItems()));
    }

    @Subscribe
    public void blockPlace(PlayerPlaceBlockEvent event) {
        Causes.BLOCK_PLACE.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar(event.getBlock())
                .eventVar("Replaced", event.getReplacementBlock()));
    }

    @Subscribe
    public void blockBreak(PlayerBreakBlockEvent event) {
        Causes.BLOCK_BREAK.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar(event.getBlock())
                .eventVar("Replaced", event.getReplacementBlock())
                .eventVar("Exp", event.getExp()));
    }

    @Subscribe(order = Order.LATE)
    public void command(CommandEvent event) {
        CommandSource source = event.getSource();
        Causes.COMMAND.activate(ScriptInstance.builder()
                .event(event)
                .eventVar((source instanceof Player ? (Player) source : null))
                .eventVar("Command", event.getCommand())
                .eventVar("Arguments", event.getArguments()));
    }
}
