package com.pqqqqq.directscript.lang;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.data.Sequencer;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.error.ErrorHandler;
import com.pqqqqq.directscript.lang.reader.Reader;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptsFile;
import com.pqqqqq.directscript.lang.util.StringParser;

import java.util.Set;

/**
 * Created by Kevin on 2015-06-18.
 * <p>The main language class for DirectScript.</p>
 * <p>This class contains getters and handlers for usage across scripts and statements.</p>
 */
public class Lang extends Environment {
    private static Lang INSTANCE;
    private Set<ScriptsFile> scriptsFiles;

    /**
     * Use {@link Lang#instance()} instead
     */
    @Deprecated
    public Lang() {
        super(DirectScript.instance());
        INSTANCE = this;
    }

    /**
     * Gets the {@link Lang} instance
     *
     * @return the lang instance
     */
    public static Lang instance() {
        return INSTANCE;
    }

    /**
     * <p>Gets the {@link Sequencer} instance.</p>
     * <p>This is analogous to: <code>Sequencer.instance()</code></p>
     *
     * @return the sequencer instance
     */
    public Sequencer sequencer() {
        return Sequencer.instance();
    }

    /**
     * <p>Gets the {@link Reader} instance.</p>
     * <p>This is analogous to: <code>Reader.instance()</code></p>
     *
     * @return
     */
    public Reader reader() {
        return Reader.instance();
    }

    /**
     * <p>Gets the {@link ErrorHandler} instance.</p>
     * <p>This is analogous to: <code>ErrorHandler.instance()</code></p>
     *
     * @return the error handler
     */
    public ErrorHandler errorHandler() {
        return ErrorHandler.instance();
    }

    /**
     * <p>Gets the {@link StringParser} instance.</p>
     * <p>This is analogous to: <code>StringParser.instance()</code></p>
     *
     * @return the string parser
     */
    public StringParser stringParser() {
        return StringParser.instance();
    }

    /**
     * Gets the {@link Set} of {@link ScriptsFile} cached after the last {@link #reloadScripts()}
     *
     * @return the cached scripts
     */
    public Set<ScriptsFile> getScriptsFiles() {
        return scriptsFiles;
    }

    /**
     * Reloads the scripts and re-attaches the error handler
     */
    public void reloadScripts() {
        errorHandler().attach(); // Attach error handler
        scriptsFiles = reader().load(); // Reload scripts
    }

    /**
     * <p>Gets an {@link Optional} {@link Script} by its given file path from root and name.</p>
     * <p>If the script exists at the root file (/scripts), the name will be <code>SCRIPTFILE:SCRIPTNAME</code>.</p>
     * <p>Otherwise, if the script exists in folders branching from /scripts, the name will be <code>PATH/SCRIPTFILE:SCRIPTNAME</code>.</p>
     * <p>The <code>PATH</code> will be from the /scripts folder.</p>
     * <p></p>
     * <p>Example: A script file test.ds in the folder hello, which is in the folder goodbye, which is in scripts:</p>
     * <p><code>goodbye/hello:test</code></p>
     *
     * @param str the script name
     * @return the script, or {@link Optional#absent()}
     */
    public Optional<Script> getScript(String str) {
        String[] split = str.split(":");

        if (split.length < 2) {
            return Optional.absent();
        }

        for (ScriptsFile scriptsFile : getScriptsFiles()) {
            if (scriptsFile.getStringRepresentationNoExt().equals(split[0])) {
                return scriptsFile.getScript(split[1]);
            }
        }

        return Optional.absent();
    }
}
