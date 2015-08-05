package com.pqqqqq.directscript.lang.statement.generic.getters;

import com.pqqqqq.directscript.lang.reader.Context;
import com.pqqqqq.directscript.lang.statement.Statement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2015-08-04.
 * Reads from a local file
 */
public class ReadFileStatement extends Statement<String[]> {

    public ReadFileStatement() {
        super(Syntax.builder()
                .identifiers("readfile")
                .arguments(Arguments.of(Argument.from("FileName")))
                .build());
    }

    @Override
    public Result<String[]> run(Context ctx) {
        List<String> lines = new ArrayList<String>();
        String fileName = ctx.getLiteral("FileName").getString();
        File file = new File(fileName);

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            try {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            } finally {
                br.close();
            }
        } catch (Exception e) {
            return Result.failure();
        }

        return Result.<String[]>builder().success().result(lines.toArray(new String[lines.size()])).build();
    }
}
