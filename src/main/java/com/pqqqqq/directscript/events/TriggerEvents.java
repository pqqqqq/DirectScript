package com.pqqqqq.directscript.events;

import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.*;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by Kevin on 2015-06-03.
 */
public class TriggerEvents {

    @Listener(order = Order.LAST)
    public void join(ClientConnectionEvent.Join event) {
        Causes.PLAYER_JOIN.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", event.getTargetEntity()));
    }

    @Listener(order = Order.LAST)
    public void quit(ClientConnectionEvent.Disconnect event) {
        Optional<Text> message = event.getMessage();
        Causes.PLAYER_QUIT.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", event.getTargetEntity())
                .eventVar("QuitMessage", (message == null ? null : TextSerializers.PLAIN.serialize(message.get()))));
    }

    @Listener(order = Order.LAST)
    public void death(DestructEntityEvent.Death event, @First Player player) {
        Optional<Text> message = event.getMessage();
        Causes.PLAYER_DEATH.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", player)
                    /*.eventVar("KeepInventory", event.keepsInventory())
                    .eventVar("KeepsLevel", event.keepsLevel())
                    .eventVar("NewExp", event.getNewExperience())
                    .eventVar("NewLevel", event.getNewLevel())*/
                .eventVar("DeathMessage", (message == null ? null : TextSerializers.PLAIN.serialize(message.get()))));
    }

    @Listener(order = Order.LAST)
    public void respawn(RespawnPlayerEvent event) {
        Causes.PLAYER_RESPAWN.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", event.getTargetEntity())
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
                    .eventCause(event.getCause())
                    .eventVar("Player", event.getTargetEntity())
                    .eventVar("From", event.getFromTransform())
                    .eventVar("To", event.getToTransform()));
        }
    }

    @Listener
    public void chat(MessageChannelEvent.Chat event, @First Player player) {
        Optional<Text> message = event.getMessage();
        Causes.PLAYER_CHAT.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", player)
                .eventVar("Message", (message == null ? null : TextSerializers.PLAIN.serialize(message.get()))));
    }

    @Listener
    public void useItem(UseItemStackEvent.Start event, @First Player player) {
        Causes.PLAYER_USE_ITEM.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", player)
                .eventVar("OriginalDuration", event.getOriginalRemainingDuration())
                .eventVar("Duration", event.getRemainingDuration())
                .eventVar("From", event.getItemStackInUse().getOriginal())
                .eventVar("To", event.getItemStackInUse().getFinal()));
    }

    /*@Listener
    public void consume(PlayerItemConsumeEvent event) {
        Causes.PLAYER_CONSUME.activate(ScriptInstance.builder()
                .event(event)
                .eventVar(event.getUser())
                .eventVar("ItemStack", event.getConsumedItem()));
    }*/

    @Listener
    public void damageEntity(DamageEntityEvent event) {
        Entity attacker = null;
        DamageType damageType = null;

        Optional<DamageSource> damageSourceOptional = event.getCause().get(DamageEntityEvent.SOURCE, DamageSource.class); // Is this ever empty?
        if (damageSourceOptional.isPresent()) {
            damageType = damageSourceOptional.get().getType();

            // We only care about entity damage sources
            if (damageSourceOptional.get() instanceof EntityDamageSource) {
                EntityDamageSource entityDamageSource = (EntityDamageSource) damageSourceOptional.get();
                attacker = entityDamageSource.getSource();

                if (attacker instanceof Projectile) { // Projectiles can use their source
                    ProjectileSource projectileSource = ((Projectile) attacker).getShooter();
                    if (projectileSource instanceof Entity) {
                        attacker = (Entity) projectileSource;
                    }
                }
            }
        }

        Entity entity = event.getTargetEntity();
        if (entity instanceof Player) { // If the hurt entity is a player, hurt cause
            Causes.PLAYER_HURT.activate(ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Hurt", entity)
                    .eventVar("Damaged", entity) // alias
                    .eventVar("Attacker", attacker)
                    .eventVar("Damage", event.getFinalDamage())
                    .eventVar("Death", event.willCauseDeath())
                    .eventVar("Type", damageType));
        }

        if (attacker != null && attacker instanceof Player) { // If attacker is a player, attack cause
            Causes.PLAYER_ATTACK.activate(ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Hurt", entity)
                    .eventVar("Damaged", entity) // alias
                    .eventVar("Attacker", attacker)
                    .eventVar("Damage", event.getFinalDamage())
                    .eventVar("Death", event.willCauseDeath())
                    .eventVar("Type", damageType));
        }
    }

    @Listener
    public void interactBlock(InteractBlockEvent event, @First Player player) {
        Causes.PLAYER_INTERACT_BLOCK.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", player)
                .eventVar("Block", event.getTargetBlock())
                .eventVar("Interaction", event instanceof InteractBlockEvent.Primary ? "Left" : "Right"));
    }

    @Listener
    public void interactEntity(InteractEntityEvent event, @First Player player) {
        Causes.PLAYER_INTERACT_ENTITY.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Player", player)
                .eventVar("Interacter", player)
                .eventVar("Interactor", player) // Alias
                .eventVar("Interacted", event.getTargetEntity())
                .eventVar("Interaction", event instanceof InteractBlockEvent.Primary ? "Left" : "Right"));
    }

    @Listener
    public void itemPickup(ChangeInventoryEvent.Pickup event, @First Player player) {
        for (SlotTransaction transaction : event.getTransactions()) {
            Causes.ITEM_PICKUP.activate(ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Player", player)
                    .eventVar("From", transaction.getOriginal())
                    .eventVar("To", transaction.getFinal()));
        }
    }

    @Listener
    public void itemDrop(DropItemEvent.Dispense event, @First Player player) {
        for (int i = 0; i < event.getEntities().size(); i++) {
            Causes.ITEM_DROP.activate(ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Player", player)
                    .eventVar("Entity", event.getEntities().get(i))
                    .eventVar("Snapshot", event.getEntitySnapshots().get(i))
                    .eventVar("World", event.getTargetWorld()));
        }
    }

    @Listener
    public void blockChange(ChangeBlockEvent event) { // Includes place, break, etc.
        Optional<Player> player = event.getCause().first(Player.class);
        ScriptInstance.Builder builder = null;

        if (player.isPresent()) {
            builder = ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Player", player.get())
                    .eventVar("Root", player.get())
                    .eventVar("Transactions", event.getTransactions())
                    .eventVar("World", event.getTargetWorld());

            if (event instanceof ChangeBlockEvent.Break) {
                Causes.PLAYER_BLOCK_BREAK.activate(builder);
            } else if (event instanceof ChangeBlockEvent.Place) {
                Causes.PLAYER_BLOCK_PLACE.activate(builder);
            }
        } else {
            Optional<Entity> entity = event.getCause().first(Entity.class);

            if (entity.isPresent()) {
                builder = ScriptInstance.builder()
                        .event(event)
                        .eventCause(event.getCause())
                        .eventVar("Entity", entity.get())
                        .eventVar("Transactions", event.getTransactions())
                        .eventVar("World", event.getTargetWorld());

                if (event instanceof ChangeBlockEvent.Break) {
                    Causes.BLOCK_BREAK.activate(builder);
                } else if (event instanceof ChangeBlockEvent.Place) {
                    Causes.BLOCK_PLACE.activate(builder);
                }
            }
        }
    }

    @Listener
    public void entitySpawn(SpawnEntityEvent event) {
        for (int i = 0; i < event.getEntities().size(); i++) {
            Causes.ENTITY_SPAWN.activate(ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Entity", event.getEntities().get(i))
                    .eventVar("Snapshot", event.getEntitySnapshots().get(i))
                    .eventVar("World", event.getTargetWorld()));
        }
    }

    // COMMANDS

    @Listener(order = Order.LATE)
    public void playerCommand(SendCommandEvent event, @First CommandSource commandSource) {
        if (commandSource instanceof Player) {
            Causes.PLAYER_COMMAND.activate(ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Player", commandSource)
                    .eventVar("Command", event.getCommand())
                    .eventVar("Arguments", event.getArguments()));
        } else {
            Causes.COMMAND.activate(ScriptInstance.builder()
                    .event(event)
                    .eventCause(event.getCause())
                    .eventVar("Source", commandSource)
                    .eventVar("Command", event.getCommand())
                    .eventVar("Arguments", event.getArguments()));
        }
    }

    @Listener
    public void explosion(ExplosionEvent.Pre event) {
        Causes.EXPLOSION.activate(ScriptInstance.builder()
                .event(event)
                .eventCause(event.getCause())
                .eventVar("Explosion", event.getExplosion())
                .eventVar("World", event.getTargetWorld()));
    }

    /*@Listener(order = Order.BEFORE_POST)
    public void changeDataHolder(ChangeDataHolderEvent.ValueChange event) {

        Lang.instance().exceptionHandler().log(event.getTargetHolder().toString());
        if (event.getTargetHolder() instanceof Player) {
            Player player = (Player) event.getTargetHolder();

            Lang.instance().exceptionHandler().log(player.getName());

            //Lang.instance().exceptionHandler().log();
            for (ImmutableValue<?> value : event.getEndResult().getSuccessfulData()) {
                Lang.instance().exceptionHandler().log(value.toString());
                Lang.instance().exceptionHandler().log(value.get().toString());
            }
            Lang.instance().exceptionHandler().flush();
        }
    }*/
}
