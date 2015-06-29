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
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.service.event.EventManager;

import java.io.File;

/**
 * Created by Kevin on 2015-06-02.
 */
@Plugin(id = DirectScript.ID, name = DirectScript.NAME, version = DirectScript.VERSION)
public class DirectScript extends Environment {
    public static final String ID = "directscript";
    public static final String NAME = "DirectScript";
    public static final String VERSION = "1.0-SNAPSHOT";
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

    @Inject
    public DirectScript(Logger logger) {
        INSTANCE = this;
        this.logger = logger;
    }

    public static DirectScript instance() {
        return INSTANCE;
    }

    @SuppressWarnings("deprecation")
    @Subscribe
    public void init(InitializationEvent event) {
        cfg = new Config(configFile, configLoader);
        cfg.init();
        cfg.load();

        new Lang(); // Create a new lang here
        Lang.instance().reloadScripts(); // Register all scripts

        // Register commands
        CommandService commandService = game.getCommandDispatcher();
        commandService.register(this, CommandDirectScript.build(this), "script", "directscript", "scr");

        // Register events
        EventManager eventManager = game.getEventManager();
        eventManager.register(this, new TriggerEvents(this));

        // Schedule events
        game.getScheduler().getTaskBuilder().delay(5L).interval(5L).execute(new InternalTimer()).name("ScriptTimer").submit(this);
    }

    @Subscribe
    public void serverStarting(ServerStartingEvent event) {
        Causes.SERVER_STARTING.activate(); // Trigger server starting cause
    }

    @Subscribe
    public void serverStopping(ServerStoppingEvent event) {
        Causes.SERVER_STOPPING.activate(); // Trigger server stopping cause
        Lang.instance().errorHandler().close(); // Close error handler stream
        cfg.save();
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
