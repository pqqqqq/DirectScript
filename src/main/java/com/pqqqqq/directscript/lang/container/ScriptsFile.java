package com.pqqqqq.directscript.lang.container;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.util.Utilities;

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

    /**
     * Creates a new script file with the given root script {@link File} and specific file
     *
     * @param root the root file
     * @param file the actual file
     */
    public ScriptsFile(File root, File file) {
        this.root = root;
        this.file = file;
    }

    /**
     * Gets the root {@link File} for scripts
     *
     * @return the root file
     */
    public File getRoot() {
        return root;
    }

    /**
     * Gets the actual {@link File} for this instance
     *
     * @return the actual file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets a {@link Set} of {@link Script}s contained in this {@link ScriptsFile}
     *
     * @return a set of scripts
     */
    public Set<Script> getScripts() {
        return scripts;
    }

    /**
     * Gets an {@link Optional} {@link Script} by its corresponding name
     *
     * @param name the name to check against
     * @return the script
     */
    public Optional<Script> getScript(String name) {
        for (Script script : scripts) {
            if (script.getName().equals(name)) {
                return Optional.of(script);
            }
        }

        return Optional.absent();
    }

    /**
     * Gets a string representation of this {@link ScriptsFile}, from the root {@link File} to the actual file, where files are separated by a '/'
     *
     * @return a string representation
     */
    public String getStringRepresentation() {
        return Utilities.getFileDiff(root, file);
    }

    /**
     * Gets a string representation of this {@link ScriptsFile}, from the root {@link File} to the actual file, where files are separated by a '/', but excluding the extension of the actual file
     * @return a string representation
     */
    public String getStringRepresentationNoExt() {
        String stringRep = getStringRepresentation();
        return stringRep.substring(0, stringRep.lastIndexOf('.'));
    }
}
