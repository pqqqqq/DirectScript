package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.statements.generic.PrintStatement;
import com.pqqqqq.directscript.lang.statement.statements.generic.VarDeclaration;
import com.pqqqqq.directscript.lang.statement.statements.internal.ScriptDeclaration;
import com.pqqqqq.directscript.lang.statement.statements.internal.ScriptTermination;
import com.pqqqqq.directscript.lang.statement.statements.option.TriggerStatement;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.util.RegistryUtil;

import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Statements {

    // Internal statements
    public static final IStatement<String> SCRIPT_DECLARATION = new ScriptDeclaration();
    public static final IStatement SCRIPT_TERMINATION = new ScriptTermination();

    // Generic statements
    public static final IStatement PRINT = new PrintStatement();
    public static final IStatement VAR_DECLARATION = new VarDeclaration();

    // Option script statements
    public static final IStatement<Trigger> TRIGGER = new TriggerStatement();

    private static final List<IStatement> REGISTRY;
    static {
        REGISTRY = RegistryUtil.getAllOf(IStatement.class, Statements.class);
    }

    public static List<IStatement> getRegistry() {
        return REGISTRY;
    }

    public static Optional<Statement> getStatementFromLine(Line line) {
        for (IStatement statement : REGISTRY) {
            Statement statementAnnot = getAnnotationFromInterface(statement);

            // TODO: Any comment stuff and/or other literal stuff that conflicts with the way this currently works
            for (String identifier : statementAnnot.identifiers()) {
                if (line.getLine().trim().startsWith(statementAnnot.prefix() + identifier) && (statementAnnot.suffix().isEmpty() || line.getLine().trim().endsWith(statementAnnot.suffix()))) {
                    return Optional.of(statementAnnot);
                }
            }
        }

        return Optional.absent();
    }

    public static Optional<IStatement> getIStatementFromLine(Line line) {
        for (IStatement statement : REGISTRY) {
            Statement statementAnnot = getAnnotationFromInterface(statement);

            // TODO: Any comment stuff and/or other literal stuff that conflicts with the way this currently works
            for (String identifier : statementAnnot.identifiers()) {
                if (line.getLine().trim().startsWith(statementAnnot.prefix() + identifier) && (statementAnnot.suffix().isEmpty() || line.getLine().trim().endsWith(statementAnnot.suffix()))) {
                    return Optional.of(statement);
                }
            }
        }

        return Optional.absent();
    }

    public static boolean isApplicableToLine(IStatement statement, Line line) {
        // TODO: Any comment stuff and/or other literal stuff that conflicts with the way this currently works
        Statement statementAnnot = getAnnotationFromInterface(statement);
        for (String identifier : statementAnnot.identifiers()) {
            if (line.getLine().trim().startsWith(statementAnnot.prefix() + identifier) && (statementAnnot.suffix().isEmpty() || line.getLine().trim().endsWith(statementAnnot.suffix()))) {
                return true;
            }
        }

        return false;
    }

    public static Statement getAnnotationFromInterface(IStatement istatement) {
        return istatement.getClass().getAnnotation(Statement.class);
    }
}
