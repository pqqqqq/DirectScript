package com.pqqqqq.directscript.lang.statement;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.generic.getters.*;
import com.pqqqqq.directscript.lang.statement.generic.setters.*;
import com.pqqqqq.directscript.lang.statement.internal.setters.*;
import com.pqqqqq.directscript.lang.statement.sponge.getters.*;
import com.pqqqqq.directscript.lang.statement.sponge.setters.*;
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
    public static final Statement TIMER = new TimerStatement();
    public static final Statement COMMAND = new CommandStatement();

    // Generic statements
    public static final Statement PRINT = new PrintStatement();
    public static final Statement<Object> VAR_DECLARATION = new VarStatement();
    public static final Statement<Object> SET = new SetStatement();
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
    public static final Statement<String> SUBSTRING = new SubstringStatement();
    public static final Statement<Boolean> CONTAINS = new ContainsStatement();
    public static final Statement<Boolean> STARTS_WITH = new StartsWithStatement();
    public static final Statement<Boolean> ENDS_WITH = new EndsWithStatement();
    public static final Statement<Integer> FIND = new FindStatement();
    public static final Statement<String> LOWERCASE = new LowercaseStatement();
    public static final Statement<String> UPPERCASE = new UppercaseStatement();
    public static final Statement<String> REPLACE = new ReplaceStatement();
    public static final Statement<String> CHARACTER = new CharacterStatement();
    public static final Statement<Integer> ORDINAL = new OrdinalStatement();
    public static final Statement<Double> INCREMENT = new IncrementStatement();
    public static final Statement<Double> DECREMENT = new DecrementStatement();
    public static final Statement<Double> POSTFIX_INCREMENT = new PostfixIncrementStatement();
    public static final Statement<Double> POSTFIX_DECREMENT = new PostfixDecrementStatement();
    public static final Statement<Object> EVENT_VAR = new EventVarStatement();

    // Sponge statements
    public static final Statement SEND = new SendStatement();
    public static final Statement BROADCAST = new BroadcastStatement();
    public static final Statement CANCEL = new CancelStatement();
    public static final Statement KICK = new KickStatement();
    public static final Statement TELEPORT = new TeleportStatement();
    public static final Statement ROTATE = new RotateStatement();
    public static final Statement CLOSE_INVENTORY = new CloseInventoryStatement();
    public static final Statement CONSOLE = new ConsoleStatement();
    public static final Statement PLAYER_COMMAND = new PlayerCommandStatement();
    public static final Statement SET_HEALTH = new SetHealthStatement();
    public static final Statement SET_HUNGER = new SetHungerStatement();
    public static final Statement SET_BLOCK = new SetBlockStatement();
    public static final Statement SPAWN = new SpawnStatement();
    public static final Statement GIVE = new GiveStatement();

    public static final Statement<Boolean> PERMISSION = new PermissionStatement();
    public static final Statement<Boolean> ONLINE = new OnlineStatement();
    public static final Statement<Object> LOCATION = new LocationStatement();
    public static final Statement<Object> VECTOR = new VectorStatement();
    public static final Statement<Object> BLOCK = new BlockStatement();
    public static final Statement<Object> ITEM = new ItemStackStatement();
    public static final Statement<Object> WORLD = new WorldStatement();
    public static final Statement<Object> PLAYER = new PlayerStatement();
    public static final Statement<Object> LIVING = new LivingStatement();
    public static final Statement<Object> ENTITY = new EntityStatement();

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
     * Gets an {@link Optional} {@link Statement} for the {@link Line}, such that {@link com.pqqqqq.directscript.lang.statement.Statement.Syntax#matches(String)} is true
     *
     * @param line the line
     * @return the statement
     */
    public static Optional<Statement> getStatement(String line) {
        for (Statement statement : REGISTRY) {
            if (!statement.getClass().isAnnotationPresent(Statement.Concept.class) && statement.getSyntax().matches(line)) { // Concept statements are excluded
                return Optional.of(statement);
            }
        }

        return Optional.absent();
    }
}
