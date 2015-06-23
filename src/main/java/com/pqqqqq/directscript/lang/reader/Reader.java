package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.script.Script;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.script.ScriptsFile;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.generic.setters.ElseStatement;
import com.pqqqqq.directscript.lang.statement.internal.setters.ScriptDeclaration;
import com.pqqqqq.directscript.lang.statement.internal.setters.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Kevin on 2015-06-02.
 * A reader for retrieving scripts from any directory
 */
public class Reader {
    private static final Reader INSTANCE = new Reader();
    private static final File SCRIPTS = new File("scripts/");

    private Reader() {
    }

    /**
     * Gets the {@link Reader} instance
     *
     * @return the instance
     */
    public static Reader instance() {
        return INSTANCE;
    }

    /**
     * Gets the default scripts {@link File}. The file is at <code>scripts/</code>
     *
     * @return the scripts file directory
     */
    public static File scriptsFile() {
        return SCRIPTS;
    }

    /**
     * Loads and a retrieves a {@link Set} of {@link ScriptsFile}s, also clears any {@link Cause} triggers
     *
     * @return the loaded set
     */
    public Set<ScriptsFile> load() {
        for (Cause cause : Causes.getRegistry()) {
            cause.getTriggers().clear(); // Clear all the triggers
        }

        return readInDir();
    }

    /**
     * Reads and retrieves a {@link Set} of {@link ScriptsFile}s in the /scripts directory
     *
     * @return the set of scripts files
     * @see #scriptsFile()
     */
    public Set<ScriptsFile> readInDir() { // Reads from ROOT/scripts
        SCRIPTS.mkdir(); // Make /scripts if it doesn't exist
        return readInDir(SCRIPTS);
    }

    /**
     * Reads and retrieves a {@link Set} of {@link ScriptsFile}s in the given directory
     *
     * @return the set of scripts files
     */
    public Set<ScriptsFile> readInDir(File dir) {
        return readInDir(dir, dir);
    }

    private Set<ScriptsFile> readInDir(File root, File dir) {
        checkNotNull(dir, "Directory cannot be null");
        checkState(dir.exists(), "The directory must exist");
        checkState(dir.isDirectory(), "Directory must actually be a directory");

        Set<ScriptsFile> scriptsFiles = new HashSet<ScriptsFile>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scriptsFiles.addAll(readInDir(root, file)); // I <3 recursion
            } else {
                if (file.getName().endsWith(".ds") || file.getName().endsWith(".dsc")) {
                    scriptsFiles.add(readScriptsFile(root, file));
                }
            }
        }

        return scriptsFiles;
    }

    /**
     * Reads and returns a {@link ScriptsFile} for the given {@link File}
     *
     * @param root the root file (eg {@link #scriptsFile()})
     * @param file the file to read
     * @return the new scripts file
     */
    public ScriptsFile readScriptsFile(File root, File file) {
        checkNotNull(file, "File cannot be null");
        checkState(file.exists(), "The file must exist");
        checkState(!file.isDirectory(), "The file cannot be a directory");
        checkState(file.getName().endsWith(".ds") || file.getName().endsWith(".dsc"), "File must have .ds or .dsc extension");

        ScriptsFile scriptsFile = new ScriptsFile(root, file);

        // Read and separate scripts
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int absoluteLine = 0, scriptLine = 0;
            boolean blockComment = false;
            Script currentScript = null;
            String line;

            List<Line> bracesLineList = new ArrayList<Line>();

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    absoluteLine++; // Up the line #s

                    Pair<Boolean, String> comments = Lang.instance().stringParser().removeComments(blockComment, line);
                    blockComment = comments.getLeft();
                    line = comments.getRight();

                    Line lineInst = new Line(absoluteLine, scriptLine, line);
                    if (lineInst.getLine().isEmpty()) {
                        continue;
                    }

                    checkState(lineInst.isRunnable(), "Unknown statement.");
                    if (currentScript != null) {
                        scriptLine++;
                        if (lineInst.getStatement() instanceof Termination || lineInst.getStatement() instanceof ElseStatement) {
                            Line startLine = bracesLineList.remove(0); // This is the opening brace line

                            startLine.setClosingBrace(lineInst); // Set the closing brace as the current line
                            lineInst.setOpeningBrace(startLine); // Set the opening brace as the start line
                            startLine.generateInternalBlock(currentScript);
                        }

                        if (lineInst.getStatement().getSyntax().getSuffix().equals("{") || lineInst.getLine().endsWith("{")) { // Necessary for else and else if statements
                            bracesLineList.add(0, lineInst);
                        }
                    }

                    Statement statementOptional = lineInst.getStatement();
                    if (statementOptional instanceof ScriptDeclaration) { // Check if this is a script declaration
                        checkState(currentScript == null, "Please end a script declaration with an end brace (})");

                        Statement.Result<String> result = lineInst.toContex(ScriptInstance.compile(null)).run();
                        checkState(result.isSuccess() && result.getResult().isPresent(), String.format("File %s has an improper formatted script declaration", file.getName()));

                        currentScript = new Script(scriptsFile, result.getResult().get());
                        bracesLineList.add(0, lineInst); // Add braces here since it wasn't added above
                        continue;
                    } else if (statementOptional instanceof Termination) {
                        checkNotNull(currentScript, "No script is being declared line " + lineInst.getLine() + " " + lineInst.getAbsoluteNumber());

                        Line starting = lineInst.getOpeningBrace();
                        if (starting.getStatement() instanceof ScriptDeclaration) {
                            scriptsFile.getScripts().add(currentScript);
                            ScriptInstance.compile(currentScript).execute();

                            // Reset cache for the script
                            currentScript = null;
                            bracesLineList.clear();
                            scriptLine = 0;
                            continue;
                        }
                    }

                    if (currentScript != null) { // Otherwise, if there's an active script, add the line to the script line list
                        currentScript.getLines().add(lineInst);
                    }
                } catch (Throwable e1) {
                    Lang.instance().errorHandler().log(String.format("Error in compilation of %s at line %d", scriptsFile.getStringRepresentationNoExt() + (currentScript != null ? " -> " + currentScript.getName() : ""), absoluteLine));
                    Lang.instance().errorHandler().log(e1);
                    Lang.instance().errorHandler().flush();
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scriptsFile;
    }
}
