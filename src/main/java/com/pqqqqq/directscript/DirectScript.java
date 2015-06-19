package com.pqqqqq.directscript;

import com.google.inject.Inject;
import com.pqqqqq.directscript.commands.CommandDirectScript;
import com.pqqqqq.directscript.events.TriggerEvents;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.script.ScriptsFile;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.event.EventManager;

import java.util.Set;

/**
 * Created by Kevin on 2015-06-02.
 */
@Plugin(id = DirectScript.ID, name = DirectScript.NAME, version = DirectScript.VERSION)
public class DirectScript {
    public static final String ID = "directscript";
    public static final String NAME = "DirectScript";
    public static final String VERSION = "1.0-SNAPSHOT";
    public static final String AUTHORS = "Pqqqqq";

    private static DirectScript INSTANCE;

    private Set<ScriptsFile> scriptsFiles;
    private Lang lang;

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @Inject
    public DirectScript(Logger logger) {
        this.logger = logger;
    }

    public static DirectScript instance() {
        return INSTANCE;
    }

    @Subscribe
    public void init(InitializationEvent event) {
        INSTANCE = this;

        lang = Lang.instance();
        lang.reloadScripts(); // Register all scripts

        // Register commands
        CommandService commandService = game.getCommandDispatcher();
        commandService.register(this, CommandDirectScript.build(this), "script", "directscript", "scr");

        // Register events
        EventManager eventManager = game.getEventManager();
        eventManager.register(this, new TriggerEvents(this));

        // Schedule events
        game.getSyncScheduler().runRepeatingTaskAfter(this, new InternalTimer(), 5L, 5L);
    }

    @Subscribe
    public void serverStarting(ServerStartingEvent event) {
        Causes.SERVER_STARTING.activate(); // Trigger server starting cause
    }

    @Subscribe
    public void serverStopping(ServerStoppingEvent event) {
        lang.errorHandler().close(); // Close error handler stream
        Causes.SERVER_STOPPING.activate(); // Trigger server stopping cause
    }

    public Logger getLogger() {
        return logger;
    }

    public Game getGame() {
        return game;
    }

    public Set<ScriptsFile> getScriptsFiles() {
        return scriptsFiles;
    }

    public Lang getLang() {
        return lang;
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
