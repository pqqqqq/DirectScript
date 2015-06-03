package com.pqqqqq.directscript;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.pqqqqq.directscript.commands.CommandDirectScript;
import com.pqqqqq.directscript.events.TriggerEvents;
import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptsFile;
import com.pqqqqq.directscript.lang.error.ErrorHandler;
import com.pqqqqq.directscript.lang.reader.Reader;
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

        reloadScripts(); // Register all scripts

        // Register commands
        CommandService commandService = game.getCommandDispatcher();
        commandService.register(this, CommandDirectScript.build(this), "script", "directscript", "scr");

        // Register events
        EventManager eventManager = game.getEventManager();
        eventManager.register(this, new TriggerEvents(this));
    }

    @Subscribe
    public void serverStarting(ServerStartingEvent event) {
        Causes.SERVER_STARTING.trigger(); // Trigger server starting cause
    }

    @Subscribe
    public void serverStopping(ServerStoppingEvent event) {
        ErrorHandler.instance().close(); // Close error handler stream
        Causes.SERVER_STOPPING.trigger(); // Trigger server stopping cause
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

    public Optional<Script> getScript(String str) {
        String[] split = str.split(":");

        if (split.length < 2) {
            return Optional.absent();
        }

        for (ScriptsFile scriptsFile : scriptsFiles) {
            if (scriptsFile.getStringRepresentationNoExt().equals(split[0])) {
                return scriptsFile.getScript(split[1]);
            }
        }

        return Optional.absent();
    }

    public void reloadScripts() {
        scriptsFiles = Reader.instance().readInDir();
    }
}
