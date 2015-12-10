package com.pqqqqq.directscript;

import com.google.inject.Inject;
import com.pqqqqq.directscript.commands.CommandDirectScript;
import com.pqqqqq.directscript.events.TriggerEvents;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kevin on 2015-06-02.
 */
@Plugin(id = DirectScript.ID, name = DirectScript.NAME, version = DirectScript.VERSION)
public class DirectScript extends Environment {
    public static final String ID = "directscript";
    public static final String NAME = "DirectScript";
    public static final String VERSION = "2.0-SNAPSHOT";
    public static final String AUTHORS = "Pqqqqq";

    private static DirectScript INSTANCE;

    private Config cfg;

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File configFile;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @SuppressWarnings("deprecation")
    @Inject
    public DirectScript(Logger logger) {
        INSTANCE = this;
        this.logger = logger;
        new Lang(); // Create a new lang here
    }

    public static DirectScript instance() {
        return INSTANCE;
    }

    @Listener
    public void init(GameInitializationEvent event) {
        cfg = new Config(configFile, configLoader);
        cfg.init();
        cfg.load();

        Lang.instance().reloadScripts(); // Register all scripts

        // Register commands
        CommandManager commandService = game.getCommandManager();
        commandService.register(this, CommandDirectScript.build(this), "script", "directscript", "scr", "scripts");

        // Register events
        EventManager eventManager = game.getEventManager();
        eventManager.registerListeners(this, new TriggerEvents(this));

        // Schedule events
        game.getScheduler().createTaskBuilder().delay(200L, TimeUnit.MILLISECONDS).interval(200L, TimeUnit.MILLISECONDS).execute(new InternalTimer()).name("ScriptTimer").submit(this);
    }

    @Listener
    public void serverStarting(GameStartingServerEvent event) {
        Causes.SERVER_STARTING.activate(); // Trigger server starting cause
    }

    @Listener
    public void serverStopping(GameStoppingServerEvent event) {
        Causes.SERVER_STOPPING.activate(); // Trigger server stopping cause
        Lang.instance().errorHandler().close(); // Close error handler stream
    }

    public Logger getLogger() {
        return logger;
    }

    public Game getGame() {
        return game;
    }

    public Config getConfig() {
        return cfg;
    }

    // Environment override
    @Override
    public void notifyChange() {
        cfg.saveAll();
    }

    @Override
    protected void suppressNotifications(boolean suppressNotifications) {
        super.suppressNotifications(suppressNotifications);
    }

    /**
     * The internal timer runnable for timer causes
     */
    public class InternalTimer implements Runnable {

        @Override
        public void run() {
            Causes.TIMER.activate();
        }
    }
}
