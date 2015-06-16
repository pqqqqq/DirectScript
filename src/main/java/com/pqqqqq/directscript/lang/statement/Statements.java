package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.generic.getters.*;
import com.pqqqqq.directscript.lang.statement.generic.setters.*;
import com.pqqqqq.directscript.lang.statement.internal.setters.ScriptDeclaration;
import com.pqqqqq.directscript.lang.statement.internal.setters.Termination;
import com.pqqqqq.directscript.lang.statement.internal.setters.TriggerStatement;
import com.pqqqqq.directscript.lang.statement.sponge.getters.*;
import com.pqqqqq.directscript.lang.statement.sponge.setters.CancelStatement;
import com.pqqqqq.directscript.lang.statement.sponge.setters.PlayerStatement;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.util.RegistryUtil;

import java.util.List;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a registry of {@link Statement}s
 */
public class Statements {

    // Internal statements
    public static final Statement<String> SCRIPT_DECLARATION = new ScriptDeclaration();
    public static final Statement<Trigger> TRIGGER = new TriggerStatement();
    public static final Statement ELSE = new ElseStatement();
    public static final Statement TERMINATION = new Termination();

    // Generic statements
    public static final Statement PRINT = new PrintStatement();
    public static final Statement VAR_DECLARATION = new VarStatement();
    public static final Statement SET = new SetStatement();
    public static final Statement CALL = new CallStatement();
    public static final Statement<Boolean> IF = new IfStatement();
    public static final Statement WHILE = new WhileStatement();
    public static final Statement FOR = new ForStatement();
    public static final Statement FOREACH = new ForEachStatement();
    public static final Statement RETURN = new ReturnStatement();
    public static final Statement BREAK = new BreakStatement();
    public static final Statement CONTINUE = new ContinueStatement();

    public static final Statement<String[]> SPLIT = new SplitStatement();
    public static final Statement<Integer> SIZE = new SizeStatement();
    public static final Statement<String> JOIN = new JoinStatement();
    public static final Statement<String> TRIM = new TrimStatement();
    public static final Statement<Double> ROUND = new RoundStatement();
    public static final Statement<Double> CEIL = new CeilStatement();
    public static final Statement<Double> FLOOR = new FloorStatement();
    public static final Statement<Object[]> REPEAT = new RepeatStatement();
    public static final Statement<String> STRING = new StringStatement();
    public static final Statement<Double> NUMBER = new NumberStatement();
    public static final Statement<Boolean> BOOLEAN = new BooleanStatement();

    // Sponge statements
    public static final Statement PLAYER = new PlayerStatement();
    public static final Statement CANCEL = new CancelStatement();

    public static final Statement<String> PLAYER_UUID = new UUIDStatement();
    public static final Statement<String> PLAYER_NAME = new NameStatement();
    public static final Statement<Boolean> PERMISSION = new PermissionStatement();
    public static final Statement<Boolean> ONLINE = new OnlineStatement();
    public static final Statement<Double[]> PLAYERLOC = new PlayerLocStatement();

    private static final List<Statement> REGISTRY;
    static {
        REGISTRY = RegistryUtil.getAllOf(Statement.class, Statements.class);
    }

    /**
     * Gets the {@link List} of {@link Statement}s in the registry
     *
     * @return the registry
     */
    public static List<Statement> getRegistry() {
        return REGISTRY;
    }

    /**
     * Gets if the given {@link Statement} is applicable to the {@link Line}
     *
     * @param statement the statement
     * @param lineInst  the line
     * @return
     */
    public static boolean isStatementEqual(Statement statement, Line lineInst) {
        return statement.matches(lineInst);
    }

    /**
     * Gets an {@link Optional} {@link Statement} for the {@link Line}, such that {@link #isStatementEqual(Statement, Line)} is true
     *
     * @param line the line
     * @return the statement
     */
    public static Optional<Statement> getStatement(Line line) {
        for (Statement statement : REGISTRY) {
            if (isStatementEqual(statement, line)) {
                return Optional.of(statement);
            }
        }

        return Optional.absent();
    }
}
