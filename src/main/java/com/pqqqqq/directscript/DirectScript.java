package com.pqqqqq.directscript;

import com.google.inject.Inject;
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

import java.util.Set;

/**
 * Created by Kevin on 2015-06-02.
 */
@Plugin(id = DirectScript.ID, name = DirectScript.NAME, version = DirectScript.VERSION)
public class DirectScript {
    public static final String ID = "directscript";
    public static final String NAME = "DirectScript";
    public static final String VERSION = "1.0-SNAPSHOT";
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

        // Register all scripts
        scriptsFiles = Reader.instance().readInDir();

        // Register commands, events, etc.
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
}
