package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.statements.internal.ScriptDeclaration;
import com.pqqqqq.directscript.lang.statement.statements.internal.ScriptTermination;
import com.pqqqqq.directscript.util.RegistryUtil;

import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Statements {

    // Internal statements
    public static final IStatement<String> SCRIPT_DECLARATION = new ScriptDeclaration();
    public static final IStatement SCRIPT_TERMINATION = new ScriptTermination();

    private static final List<IStatement> REGISTRY;
    static {
        REGISTRY = RegistryUtil.getAllOf(IStatement.class, Statements.class);
    }

    public static List<IStatement> getRegistry() {
        return REGISTRY;
    }

    public static Optional<IStatement> getStatementFromLine(Line line) {
        for (IStatement statement : REGISTRY) {
            Statement statementAnnot = statement.getClass().getAnnotation(Statement.class);

            // TODO: Any comment stuff and/or other literal stuff that conflicts with the way this currently works
            for (String identifier : statementAnnot.identifiers()) {
                if (line.getLine().trim().startsWith(statementAnnot.prefix() + identifier)) {
                    return Optional.of(statement);
                }
            }
        }

        return Optional.absent();
    }

    public static boolean isApplicableToLine(IStatement statement, Line line) {
        // TODO: Any comment stuff and/or other literal stuff that conflicts with the way this currently works
        Statement statementAnnot = statement.getClass().getAnnotation(Statement.class);
        for (String identifier : statementAnnot.identifiers()) {
            if (line.getLine().trim().startsWith(statementAnnot.prefix() + identifier)) {
                return true;
            }
        }

        return false;
    }
}
