package com.pqqqqq.directscript.events;

import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.command.MessageSinkEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.*;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-03.
 */
public class TriggerEvents {
    private DirectScript plugin;

    public TriggerEvents(DirectScript plugin) {
        this.plugin = plugin;
    }

    @Listener(order = Order.LAST)
    public void join(ClientConnectionEvent.Join event) {
        Causes.PLAYER_JOIN.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getTargetEntity()));
    }

    @Listener(order = Order.LAST)
    public void quit(ClientConnectionEvent.Disconnect event) {
        Text message = event.getMessage();
        Causes.PLAYER_QUIT.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getTargetEntity())
                .eventVar("QuitMessage", Texts.toPlain(message)));
    }

    @Listener(order = Order.LAST)
    public void death(DestructEntityEvent.Death event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            Causes.PLAYER_DEATH.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar(player.get())
                    /*.eventVar("KeepInventory", event.keepsInventory())
                    .eventVar("KeepsLevel", event.keepsLevel())
                    .eventVar("NewExp", event.getNewExperience())
                    .eventVar("NewLevel", event.getNewLevel())*/
                    .eventVar("DeathMessage", Texts.toPlain(event.getMessage())));
        }
    }

    @Listener(order = Order.LAST)
    public void respawn(RespawnPlayerEvent event) {
        Causes.PLAYER_RESPAWN.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getTargetEntity())
                .eventVar("From", event.getFromTransform())
                .eventVar("To", event.getToTransform())
                .eventVar("Bed", event.isBedSpawn()));
    }

    @Listener(order = Order.LAST)
    public void move(DisplaceEntityEvent.TargetPlayer event) {
        Location<World> oldLoc = event.getFromTransform().getLocation(), newLoc = event.getToTransform().getLocation();
        if (oldLoc.getBlockX() != newLoc.getBlockX() || oldLoc.getBlockY() != newLoc.getBlockY() || oldLoc.getBlockZ() != newLoc.getBlockZ()) { // We only want when the block is changed
            Causes.PLAYER_MOVE.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar(event.getTargetEntity())
                    .eventVar("From", event.getFromTransform())
                    .eventVar("To", event.getToTransform()));
        }
    }

    @Listener
    public void chat(MessageSinkEvent.Chat event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            Causes.PLAYER_CHAT.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar(player.get())
                    .eventVar("Message", Texts.toPlain(event.getMessage())));
        }
    }

    /*@Listener
    public void consume(PlayerItemConsumeEvent event) {
        Causes.PLAYER_CONSUME.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar("ItemStack", event.getConsumedItem()));
    }*/

    @Listener
    public void changeHealth(DamageEntityEvent event) {
        Entity player = event.getTargetEntity();
        if (player instanceof Player) {
            Causes.PLAYER_CHANGE_HEALTH.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar((Player) player)
                    .eventVar("Damage", event.getFinalDamage())); // TODO: Causation
        }
    }

    @Listener
    public void interactBlock(InteractBlockEvent event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            Causes.PLAYER_INTERACT_BLOCK.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar(player.get())
                    .eventVar(event.getTargetBlock())
                    .eventVar("Interaction", event instanceof InteractBlockEvent.Primary ? "Left" : "Right"));
        }
    }

    @Listener
    public void interactEntity(InteractEntityEvent event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            Causes.PLAYER_INTERACT_ENTITY.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar(player.get())
                    .eventVar("Interact", event.getTargetEntity())
                    .eventVar("Interaction", event instanceof InteractBlockEvent.Primary ? "Left" : "Right"));
        }
    }

    @Listener
    public void itemPickup(ChangeInventoryEvent.Pickup event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            for (SlotTransaction transaction : event.getTransactions()) {
                Causes.ITEM_PICKUP.activate(ScriptInstance.builder()
                        .event(event)
                        .eventVar(player.get())
                        .eventVar("From", transaction.getOriginal())
                        .eventVar("To", transaction.getFinal()));
            }
        }
    }

    @Listener
    public void itemDrop(DropItemEvent.Dispense event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent()) {
            for (int i = 0; i < event.getEntities().size(); i++) {
                Causes.ITEM_DROP.activate(ScriptInstance.builder()
                        .event(event)
                        .eventVar(player.get())
                        .eventVar("Entity", event.getEntities().get(i))
                        .eventVar("Snapshot", event.getEntitySnapshots().get(i))
                        .eventVar("World", event.getTargetWorld()));
            }
        }
    }

    @Listener
    public void blockChange(ChangeBlockEvent event) { // Includes place, break, etc.
        Optional<Player> player = event.getCause().first(Player.class);
        ScriptInstance.Builder builder = null;

        if (player.isPresent()) {
            builder = ScriptInstance.builder()
                    .event(event)
                    .eventVar(player.get())
                    .eventVar("Root", player.get())
                    .eventVar("Transactions", event.getTransactions())
                    .eventVar("World", event.getTargetWorld());

            if (event instanceof ChangeBlockEvent.Break) {
                Causes.PLAYER_BLOCK_BREAK.activate(builder);
            } else if (event instanceof ChangeBlockEvent.Place) {
                Causes.PLAYER_BLOCK_PLACE.activate(builder);
            }
        } else if (event.getCause().root().isPresent()) {
            builder = ScriptInstance.builder()
                    .event(event)
                    .eventVar("Root", event.getCause().root().get())
                    .eventVar("Transactions", event.getTransactions())
                    .eventVar("World", event.getTargetWorld());

            if (event instanceof ChangeBlockEvent.Break) {
                Causes.BLOCK_BREAK.activate(builder);
            } else if (event instanceof ChangeBlockEvent.Place) {
                Causes.BLOCK_PLACE.activate(builder);
            }
        }
    }

    @Listener
    public void entitySpawn(SpawnEntityEvent event) {
        for (int i = 0; i < event.getEntities().size(); i++) {
            Causes.ENTITY_SPAWN.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar("Entity", event.getEntities().get(i))
                    .eventVar("Snapshot", event.getEntitySnapshots().get(i))
                    .eventVar("World", event.getTargetWorld()));
        }
    }

    @Listener(order = Order.LATE)
    public void command(SendCommandEvent event) {
        CommandSource source = event.getCause().first(CommandSource.class).get();

        if (source instanceof Player) { // TODO
            Causes.COMMAND.activate(ScriptInstance.builder()
                    .event(event)
                    .eventVar((Player) source)
                    .eventVar("Command", event.getCommand())
                    .eventVar("Arguments", event.getArguments()));
        }
    }

    @Listener
    public void explosion(ExplosionEvent.Pre event) {
        Causes.EXPLOSION.activate(ScriptInstance.builder()
                .event(event)
                .eventVar("Explosion", event.getExplosion())
                .eventVar("World", event.getTargetWorld()));
    }
}
