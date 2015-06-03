package com.pqqqqq.directscript.lang.container;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.util.Utilities;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a wrapper for a {@link File} that contains {@link Script}s
 */
public class ScriptsFile {
    private final File root;
    private final File file;
    private final Set<Script> scripts = new HashSet<Script>();

    public ScriptsFile(File root, File file) {
        this.root = root;
        this.file = file;
    }

    public File getRoot() {
        return root;
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

    public String getStringRepresentation() {
        return Utilities.getFileDiff(root, file);
    }

    public String getStringRepresentationNoExt() {
        String stringRep = getStringRepresentation();
        return stringRep.substring(0, stringRep.lastIndexOf('.'));
    }
}
