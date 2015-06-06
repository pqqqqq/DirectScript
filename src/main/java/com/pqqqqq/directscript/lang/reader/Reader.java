package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.DirectScript;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.container.ScriptsFile;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.statement.statements.internal.ScriptDeclaration;
import com.pqqqqq.directscript.lang.statement.statements.internal.Termination;
import com.pqqqqq.directscript.lang.trigger.cause.Cause;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

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
    private static Reader INSTANCE = new Reader();

    private Reader() {
    }

    public static Reader instance() {
        return INSTANCE;
    }

    public Set<ScriptsFile> load() {
        for (Cause cause : Causes.getRegistry()) {
            cause.getTriggers().clear(); // Clear all the triggers
        }

        return readInDir();
    }

    public Set<ScriptsFile> readInDir() { // Reads from ROOT/scripts
        File dir = new File("scripts/");
        dir.mkdir(); // Make /scripts if it doesn't exist

        return readInDir(dir);
    }

    public Set<ScriptsFile> readInDir(File dir) {
        return readInDir(dir, dir);
    }

    public Set<ScriptsFile> readInDir(File root, File dir) {
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
            Script currentScript = null;
            String line;

            List<Line> bracesLineList = new ArrayList<Line>();

            while ((line = bufferedReader.readLine()) != null) {
                // Up the line #s
                absoluteLine++;
                if (currentScript != null) {
                    scriptLine++;
                }

                Line lineInst = new Line(absoluteLine, scriptLine, line);
                if (lineInst.getLine().isEmpty() || lineInst.getLine().startsWith("//")) {
                    continue;
                }

                if (currentScript != null) {
                    Optional<Statement> statementOptional = lineInst.getStatement();
                    if (statementOptional.isPresent()) {
                        if (statementOptional.get().suffix() != null && statementOptional.get().suffix().equals("{")) {
                            bracesLineList.add(0, lineInst);
                        } else if (lineInst.getIStatement().isPresent() && lineInst.getIStatement().get() instanceof Termination) {
                            currentScript.getLinkedLines().put(bracesLineList.remove(0), lineInst);
                        }
                    }
                }

                if (Statements.isApplicable(Statements.SCRIPT_DECLARATION, lineInst)) { // Check if this is a script declaration
                    checkState(currentScript == null, "Please end a script declaration with !endscript");

                    StatementResult<String> result = Statements.SCRIPT_DECLARATION.run(ScriptInstance.compile(), lineInst);
                    checkState(result.isSuccess() && result.getResult().isPresent(), String.format("File %s has an improper formatted script declaration", file.getName()));

                    currentScript = new Script(scriptsFile, result.getResult().get());
                    bracesLineList.add(0, lineInst); // Add braces here since it wasn't added above
                } else if (Statements.isApplicable(Statements.TERMINATION, lineInst)) {
                    checkNotNull(currentScript, "No script is being declared");

                    Line starting = currentScript.lookupStartingLine(lineInst);
                    if (starting.getIStatement().isPresent() && starting.getIStatement().get() instanceof ScriptDeclaration) {
                        scriptsFile.getScripts().add(currentScript);
                        currentScript.run(ScriptInstance.builder().script(currentScript).cause(Causes.COMPILE).predicate(Script.compileTimePredicate()).build());

                        // Reset cache for the script
                        currentScript = null;
                        bracesLineList.clear();
                        scriptLine = 0;
                    } else {
                        currentScript.getLines().add(lineInst); // If not, add termination to normal script line list
                    }
                } else if (currentScript != null) {
                    currentScript.getLines().add(lineInst);
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
            DirectScript.instance().getErrorHandler().log("Error in compilation of " + file.getName());
            DirectScript.instance().getErrorHandler().log(e);
            DirectScript.instance().getErrorHandler().flush();
        }

        return scriptsFile;
    }
}
