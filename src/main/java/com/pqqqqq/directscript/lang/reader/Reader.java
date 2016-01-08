package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.exception.state.CompilationException;
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
import java.util.*;

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
     * <p>Loads and a retrieves a {@link Set} of {@link ScriptsFile}s, also clears any {@link Cause} triggers</p>
     *
     * @return the loaded set
     * @see #readInDir()
     * @see Cause#getTriggers()
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
     * @see #readInDir(File)
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

        Set<ScriptsFile> scriptsFiles = new HashSet<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scriptsFiles.addAll(readInDir(root, file)); // I <3 recursion
            } else if (file.getName().endsWith(".ds") || file.getName().endsWith(".dsc")) {
                scriptsFiles.add(readScriptsFile(root, file));
            }
        }

        return scriptsFiles;
    }

    /**
     * Reads and returns a {@link ScriptsFile} for the given {@link File} directory
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
            Block currentBlock = null;
            String line;

            Deque<Line.Builder> bracesLineList = new ArrayDeque<>();
            List<Line.Builder> cumulativeLines = new ArrayList<>();

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    Pair<Boolean, String> comments = Lang.instance().stringParser().removeComments(blockComment, line);
                    blockComment = comments.getLeft();
                    line = comments.getRight();

                    Line.Builder lineBuilder = Line.builder().absoluteLine(++absoluteLine).line(line); // Up absolute line
                    line = lineBuilder.getLine(); // Use the trimmed line
                    if (line.isEmpty()) { // Empty lines don't matter past this
                        continue;
                    }

                    Line.Content content = lineBuilder.getContent();
                    Statement statement = content.getStatement();

                    if (currentScript != null) {
                        lineBuilder.scriptLine(scriptLine); // Up script line (after to preserve base-0)
                        if (statement instanceof Termination || statement instanceof ElseStatement) { // Closing brace
                            Line.Builder startLine = bracesLineList.pollLast(); // This is the opening brace line

                            if (startLine.getScript() != null) {
                                startLine.closingBrace(scriptLine); // Set the closing brace as the current line
                                lineBuilder.openingBrace(startLine.getScriptLineNumber()); // Set the opening brace as the start line
                                currentBlock = startLine.getBlockContainer(); // Set current block to what it was before
                            }
                        }

                        lineBuilder.script(currentScript).block(currentBlock); // Set the line's block

                        if (statement.getSyntax().getSuffix().equals("{") || line.endsWith("{")) { // Opening brace
                            bracesLineList.add(lineBuilder);
                            currentBlock = new Block(bracesLineList.size() - 1); // Subtract one since script is 0
                            lineBuilder.internal(currentBlock); // Set internal block
                        }
                    }

                    if (statement instanceof ScriptDeclaration) { // Check if this is a script declaration
                        checkState(currentScript == null, "Please end a script declaration with an end brace (})");

                        Statement.Result<String> result = content.toContext(ScriptInstance.compile(null)).run();
                        checkState(result.isSuccess() && result.getResult().isPresent(), String.format("File %s has an improper formatted script declaration", file.getName()));

                        currentScript = new Script(scriptsFile, result.getResult().get());
                        currentBlock = currentScript;
                        bracesLineList.add(lineBuilder); // Add braces here since it wasn't added above
                        continue;
                    } else if (statement instanceof Termination) {
                        checkNotNull(currentScript, "No script is being declared line " + lineBuilder.getLine() + " " + lineBuilder.getAbsoluteLineNumber());
                        if (lineBuilder.getOpeningBraceLine() == null) {
                            scriptsFile.getScripts().add(currentScript);

                            // Reset cache for the script
                            currentBlock = null;
                            currentScript = null;
                            bracesLineList.clear();
                            scriptLine = 0; // Reset script line
                            continue;
                        }
                    }

                    scriptLine++; // Increment script line
                    if (currentScript != null) {
                        cumulativeLines.add(lineBuilder); // Add line builder
                    }
                }
            } catch (Throwable e1) {
                Lang.instance().exceptionHandler().log(new CompilationException(e1, "Error in compilation of %s at line %d", scriptsFile.getStringRepresentationNoExt() + (currentScript != null ? " -> " + currentScript.getName() : ""), absoluteLine));
                Lang.instance().exceptionHandler().flush();
            }

            cumulativeLines.forEach(Line.Builder::build); // Build everything
            scriptsFile.getScripts().forEach((script) -> ScriptInstance.compile(script).execute()); // Compile scripts

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scriptsFile;
    }
}
