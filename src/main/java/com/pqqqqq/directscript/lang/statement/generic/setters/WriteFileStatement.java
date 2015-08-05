package com.pqqqqq.directscript.lang.statement.generic.setters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by Kevin on 2015-08-04.
 * Writes to a local file
 */
public class WriteFileStatement extends Statement {

    public WriteFileStatement() {
        super(Syntax.builder()
                .identifiers("writefile")
                .arguments(Arguments.of(Argument.from("FileName"), ",", Argument.from("Contents")))
                .arguments(Arguments.of(Argument.from("FileName"), ",", Argument.from("Contents"), ",", Argument.from("Append")))
                .build());
    }

    @Override
    public Result run(Context ctx) {
        String fileName = ctx.getLiteral("FileName").getString();
        String contents = ctx.getLiteral("Contents").getString();
        boolean append = ctx.getLiteral("Append", false).getBoolean();
        File file = new File(fileName);

        try {
            FileWriter fw = new FileWriter(file, append);
            BufferedWriter bw = new BufferedWriter(fw);

            try {
                bw.write(contents); // Write the contents
                bw.flush(); // Flush stream
            } finally {
                bw.close(); // Close stream
            }
        } catch (Exception e) {
            return Result.failure();
        }

        return Result.success();
    }
}
