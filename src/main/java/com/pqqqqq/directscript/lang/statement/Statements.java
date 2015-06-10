package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.annotation.Statement;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.getters.generic.JoinStatement;
import com.pqqqqq.directscript.lang.statement.getters.generic.SizeStatement;
import com.pqqqqq.directscript.lang.statement.getters.generic.SplitStatement;
import com.pqqqqq.directscript.lang.statement.getters.generic.TrimStatement;
import com.pqqqqq.directscript.lang.statement.getters.sponge.GetPlayerUUID;
import com.pqqqqq.directscript.lang.statement.getters.sponge.OnlineStatement;
import com.pqqqqq.directscript.lang.statement.getters.sponge.PermissionStatement;
import com.pqqqqq.directscript.lang.statement.setters.generic.*;
import com.pqqqqq.directscript.lang.statement.setters.internal.ScriptDeclaration;
import com.pqqqqq.directscript.lang.statement.setters.internal.Termination;
import com.pqqqqq.directscript.lang.statement.setters.option.TriggerStatement;
import com.pqqqqq.directscript.lang.statement.setters.sponge.CancelStatement;
import com.pqqqqq.directscript.lang.statement.setters.sponge.PlayerStatement;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.util.RegistryUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Kevin on 2015-06-02.
 */
public class Statements {

    // Internal statements
    public static final IStatement<String> SCRIPT_DECLARATION = new ScriptDeclaration();
    public static final IStatement ELSE = new ElseStatement();
    public static final IStatement TERMINATION = new Termination();

    // Generic statements
    public static final IStatement PRINT = new PrintStatement();
    public static final IStatement VAR_DECLARATION = new VarStatement();
    public static final IStatement SET = new SetStatement();
    public static final IStatement CALL = new CallStatement();
    public static final IStatement<Boolean> IF = new IfStatement();
    public static final IStatement WHILE = new WhileStatement();
    public static final IStatement RETURN = new ReturnStatement();

    public static final IStatement<String[]> SPLIT = new SplitStatement();
    public static final IStatement<Integer> SIZE = new SizeStatement();
    public static final IStatement<String> JOIN = new JoinStatement();
    public static final IStatement<String> TRIM = new TrimStatement();

    // Option script statements
    public static final IStatement<Trigger> TRIGGER = new TriggerStatement();

    // Sponge statements
    public static final IStatement PLAYER = new PlayerStatement();
    public static final IStatement CANCEL = new CancelStatement();

    public static final IStatement<String> PLAYER_UUID = new GetPlayerUUID();
    public static final IStatement<Boolean> PERMISSION = new PermissionStatement();
    public static final IStatement<Boolean> ONLINE = new OnlineStatement();

    private static final List<IStatement> REGISTRY;
    static {
        REGISTRY = RegistryUtil.getAllOf(IStatement.class, Statements.class);
    }

    public static List<IStatement> getRegistry() {
        return REGISTRY;
    }

    public static boolean isStatementEqual(final Statement statement, Line lineInst) {
        if (!statement.useBrackets()) {
            String line = lineInst.getLine();
            for (String identifier : statement.identifiers()) {
                if (line.startsWith(statement.prefix() + identifier + " ") && (statement.suffix().isEmpty() || line.endsWith(statement.suffix())) || line.equals(statement.prefix() + identifier + statement.suffix())) {
                    return true;
                }
            }

            return false;
        }

        String identifierString = "(";

        for (String identifier : statement.identifiers()) {
            identifierString += "\\Q" + identifier + "\\E|";
        }

        identifierString = identifierString.substring(0, identifierString.length() - 1) + ")";
        String prefixString = (statement.prefix().isEmpty() ? "" : "\\Q" + statement.prefix() + "\\E");

        Pattern pattern = Pattern.compile(prefixString + identifierString + "(\\s*?)\\((.*?)\\)(\\s*?)\\Q" + statement.suffix() + "\\E");
        return pattern.matcher(lineInst.getLine()).matches();
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
