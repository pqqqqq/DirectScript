package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.statements.generic.*;
import com.pqqqqq.directscript.lang.statement.statements.internal.ScriptDeclaration;
import com.pqqqqq.directscript.lang.statement.statements.internal.Termination;
import com.pqqqqq.directscript.lang.statement.statements.option.TriggerStatement;
import com.pqqqqq.directscript.lang.statement.statements.sponge.GetPlayerUUID;
import com.pqqqqq.directscript.lang.statement.statements.sponge.PlayerStatement;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.util.RegistryUtil;

import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Statements {

    // Internal statements
    public static final IStatement<String> SCRIPT_DECLARATION = new ScriptDeclaration();
    public static final IStatement ELSE_STATEMENT = new ElseStatement();
    public static final IStatement TERMINATION = new Termination();

    // Generic statements
    public static final IStatement PRINT = new PrintStatement();
    public static final IStatement VAR_DECLARATION = new VarDeclaration();
    public static final IStatement CALL = new CallStatement();

    public static final IStatement<Boolean> IF_STATEMENT = new IfStatement();

    // Option script statements
    public static final IStatement<Trigger> TRIGGER = new TriggerStatement();

    // Sponge statements
    public static final IStatement PLAYER = new PlayerStatement();

    public static final IStatement<String> GET_PLAYER_UUID = new GetPlayerUUID();

    private static final List<IStatement> REGISTRY;
    static {
        REGISTRY = RegistryUtil.getAllOf(IStatement.class, Statements.class);
    }

    public static List<IStatement> getRegistry() {
        return REGISTRY;
    }

    public static boolean isStatementEqual(final Statement statement, Line lineInst) {
        String line = lineInst.getLine();
        for (String identifier : statement.identifiers()) {
            if (line.startsWith(statement.prefix() + identifier + " ") && (statement.suffix().isEmpty() || line.endsWith(statement.suffix())) || line.equals(statement.prefix() + identifier + statement.suffix())) {
                return true;
            }
        }

        return false;
    }

    public static Optional<Statement> getStatement(Line line) {
        for (IStatement statement : REGISTRY) {
            Statement statementAnnot = getAnnotationFromInterface(statement);
            if (isStatementEqual(statementAnnot, line)) {
                return Optional.of(statementAnnot);
            }
        }

        return Optional.absent();
    }

    public static Optional<IStatement> getIStatement(Line line) {
        for (IStatement statement : REGISTRY) {
            if (isStatementEqual(getAnnotationFromInterface(statement), line)) {
                return Optional.of(statement);
            }
        }

        return Optional.absent();
    }

    public static boolean isApplicable(IStatement statement, Line line) {
        return isStatementEqual(getAnnotationFromInterface(statement), line);
    }

    public static Statement getAnnotationFromInterface(IStatement istatement) {
        return istatement.getClass().getAnnotation(Statement.class);
    }
}
