package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.container.Script;
import com.pqqqqq.directscript.lang.container.ScriptInstance;
import com.pqqqqq.directscript.lang.container.ScriptsFile;
import com.pqqqqq.directscript.lang.statement.StatementResult;
import com.pqqqqq.directscript.lang.statement.Statements;
import com.pqqqqq.directscript.lang.trigger.cause.Causes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
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

            int absoluteLine = 0;
            int scriptLine = 0;
            Script currentScript = null;
            String line;

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

                if (Statements.isApplicableToLine(Statements.SCRIPT_DECLARATION, lineInst)) { // Check if this is a script declaration
                    checkState(currentScript == null, "Please end a script declaration with #endscript");

                    StatementResult<String> result = Statements.SCRIPT_DECLARATION.run(ScriptInstance.compile(), lineInst);
                    checkState(result.isSuccess() && result.getResult().isPresent(), String.format("File %s has an improper formatted script declaration", file.getName()));

                    currentScript = new Script(scriptsFile, result.getResult().get());
                } else if (Statements.isApplicableToLine(Statements.SCRIPT_TERMINATION, lineInst)) {
                    checkNotNull(currentScript, "No script is being declared");

                    scriptsFile.getScripts().add(currentScript);
                    currentScript.run(ScriptInstance.builder().script(currentScript).cause(Causes.COMPILE).predicate(Script.compileTimePredicate()).build());
                    currentScript = null;
                    scriptLine = 0;
                } else if (currentScript != null) {
                    currentScript.getLines().add(lineInst);
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scriptsFile;
    }
}
