package com.pqqqqq.directscript.events;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.EntitySpawnEvent;
import org.spongepowered.api.event.entity.player.*;
import org.spongepowered.api.event.message.CommandEvent;
import org.spongepowered.api.text.Text;
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

    @Subscribe(order = Order.LAST)
    public void join(PlayerJoinEvent event) {
        Causes.PLAYER_JOIN.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity()));
    }

    @Subscribe(order = Order.LAST)
    public void quit(PlayerQuitEvent event) {
        Text message = event.getNewMessage() != null ? event.getNewMessage() : event.getMessage();
        Causes.PLAYER_QUIT.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("QuitMessage", Texts.toPlain(message)));
    }

    @Subscribe(order = Order.LAST)
    public void death(PlayerDeathEvent event) {
        Text message = event.getNewMessage() != null ? event.getNewMessage() : event.getMessage();
        Causes.PLAYER_DEATH.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("KeepInventory", event.keepsInventory())
                .eventVar("KeepsLevel", event.keepsLevel())
                .eventVar("NewExp", event.getNewExperience())
                .eventVar("NewLevel", event.getNewLevel())
                .eventVar("DeathMessage", Texts.toPlain(message)));
    }

    @Subscribe(order = Order.LAST)
    public void respawn(PlayerRespawnEvent event) {
        Causes.PLAYER_RESPAWN.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar(event.getNewRespawnLocation())
                .eventVar("Bed", event.isBedSpawn()));
    }

    @Subscribe
    public void chat(PlayerChatEvent event) {
        Text message = event.getNewMessage() != null ? event.getNewMessage() : event.getMessage();
        Causes.PLAYER_CHAT.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("Message", Texts.toPlain(message)));
    }

    @Subscribe
    public void consume(PlayerItemConsumeEvent event) {
        Causes.PLAYER_CONSUME.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar("ItemStack", event.getConsumedItem()));
    }

    @Subscribe
    public void changeHealth(PlayerChangeHealthEvent event) {
        Causes.PLAYER_CHANGE_HEALTH.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("Old", event.getOldData().getHealth())
                .eventVar("New", event.getNewData().getHealth())); // TODO: Causation
    }

    @Subscribe
    public void interactBlock(PlayerInteractBlockEvent event) {
        Causes.PLAYER_INTERACT_BLOCK.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar(event.getBlock())
                .eventVar("Interaction", event.getInteractionType().getId())); // TODO: Any scenarios where getName is needed over getId? .. // TODO: Causation
    }

    @Subscribe
    public void interactEntity(PlayerInteractEntityEvent event) {
        Causes.PLAYER_INTERACT_ENTITY.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getEntity())
                .eventVar("Interact", event.getTargetEntity())
                .eventVar("Interaction", event.getInteractionType().getId())); // TODO: Any scenarios where getName is needed over getId?
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
                .eventVar("ItemStacks", event.getDroppedItems())); // TODO: Causation
    }

    @Subscribe
    public void blockPlace(PlayerPlaceBlockEvent event) {
        Causes.BLOCK_PLACE.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar(event.getBlock())
                .eventVar("Replaced", event.getReplacementBlock())); // TODO: Causation
    }

    @Subscribe
    public void blockBreak(PlayerBreakBlockEvent event) {
        Causes.BLOCK_BREAK.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar(event.getBlock())
                .eventVar("Replaced", event.getReplacementBlock())
                .eventVar("Exp", event.getExp())); // TODO: Causation
    }

    @Subscribe
    public void blockHarvest(PlayerHarvestBlockEvent event) {
        Causes.BLOCK_HARVEST.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar(event.getBlock())
                .eventVar("DroppedItems", event.getDroppedItems())
                .eventVar("DropChance", event.getDropChance())
                .eventVar("SilkTouch", event.isSilkTouch())); // TODO: Causation
    }

    @Subscribe
    public void entitySpawn(EntitySpawnEvent event) {
        Causes.ENTITY_SPAWN.activate(ScriptInstance.builder()
                .event(event)
                .eventVar("Entity", event.getEntity())
                .eventVar(event.getLocation()));
    }

    @Subscribe(order = Order.LATE)
    public void command(CommandEvent event) {
        CommandSource source = event.getSource();

        if (source instanceof Player) { // TODO
            Causes.COMMAND.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar((Player) source)
                    .eventVar("Command", event.getCommand())
                    .eventVar("Arguments", event.getArguments()));
        }
    }
}
