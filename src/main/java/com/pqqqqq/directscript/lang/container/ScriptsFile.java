package com.pqqqqq.directscript.lang.container;

import com.google.common.base.Optional;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a wrapper for a {@link File} that contains {@link Script}s
 */
public class ScriptsFile {
    private final File file;
    private final Set<Script> scripts = new HashSet<Script>();

    public ScriptsFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public Set<Script> getScripts() {
        return scripts;
    }

    public Optional<Script> getScript(String name) {
        for (Script script : scripts) {
            if (script.getName().equals(name)) {
                return Optional.of(script);
            }
        }

        return Optional.absent();
    }
}