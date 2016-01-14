package com.pqqqqq.directscript.lang.statement;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Line;
import com.pqqqqq.directscript.lang.statement.generic.getters.*;
import com.pqqqqq.directscript.lang.statement.generic.setters.*;
import com.pqqqqq.directscript.lang.statement.internal.setters.*;
import com.pqqqqq.directscript.lang.statement.sponge.getters.*;
import com.pqqqqq.directscript.lang.statement.sponge.setters.*;
import com.pqqqqq.directscript.lang.trigger.Trigger;
import com.pqqqqq.directscript.lang.util.RegistryUtil;
import org.spongepowered.api.data.DataSerializable;

import java.util.List;
import java.util.Optional;

/**
 * Created by Kevin on 2015-06-02.
 * Represents a registry of {@link Statement}s
 */
public class Statements {

    // WARNING: The order of these entries matters (top ones are first to be tested against)

    // Internal statements
    public static final Statement<String> SCRIPT_DECLARATION = new ScriptDeclaration();
    public static final Statement<Trigger> TRIGGER = new TriggerStatement();
    public static final Statement ELSE = new ElseStatement();
    public static final Statement TERMINATION = new Termination();
    public static final Statement TIMER = new TimerStatement();
    public static final Statement COMMAND = new CommandStatement();

    // Generic statements
    public static final Statement PRINT = new PrintStatement();

    // Sets
    public static final Statement<Object> SET = new SetStatement();
    public static final Statement<Object> SET_EXPLICIT = new SetStatementExplicit();
    public static final Statement<Object> SET_ADD = new SetAddStatement();
    public static final Statement<Object> SET_SUB = new SetSubStatement();
    public static final Statement<Object> SET_MULT = new SetMultStatement();
    public static final Statement<Object> SET_DIV = new SetDivStatement();
    public static final Statement<Object> SET_POW = new SetPowStatement();
    public static final Statement<Object> SET_ROOT = new SetRootStatement();

    public static final Statement CALL = new CallStatement();
    public static final Statement<Boolean> IF = new IfStatement();
    public static final Statement WHILE = new WhileStatement();
    public static final Statement SCHEDULE = new ScheduleStatement();
    public static final Statement FOR = new ForStatement();
    public static final Statement FOREACH = new ForEachStatement();
    public static final Statement FORKV = new ForKVStatement();
    public static final Statement RETURN = new ReturnStatement();
    public static final Statement BREAK = new BreakStatement();
    public static final Statement CONTINUE = new ContinueStatement();
    public static final Statement REMOVE = new RemoveStatement();
    public static final Statement REMOVE_INDEX = new RemoveIndexStatement();
    public static final Statement WRITE_FILE = new WriteFileStatement();

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
    public static final Statement<Integer> RANDOM_INTEGER = new RandomIntStatement();
    public static final Statement<Double> RANDOM_DOUBLE = new RandomDoubleStatement();
    public static final Statement<Long> MILLIS = new MillisStatement();
    public static final Statement<String[]> READ_FILE = new ReadFileStatement();
    public static final Statement<Object> SCRIPT_GETTER = new ScriptStatement();
    public static final Statement<String> DATE = new DateStatement();
    public static final Statement<List<Literal>> SUBLIST = new SublistStatement();
    public static final Statement<Double> ABS = new AbsStatement();

    // Sponge statements
    public static final Statement SEND = new SendStatement();
    public static final Statement BROADCAST = new BroadcastStatement();
    public static final Statement CANCEL = new CancelStatement();
    public static final Statement CONSOLE = new ConsoleStatement();
    public static final Statement EQUIP = new EquipStatement();

    public static final Statement<Boolean> ONLINE = new OnlineStatement();
    public static final Statement<Object> LOCATION = new LocationStatement();
    public static final Statement<Object> VECTOR = new VectorStatement();
    public static final Statement<Object> BLOCK = new BlockStatement();
    public static final Statement<Object> ITEM = new ItemStackStatement();
    public static final Statement<Object> WORLD = new WorldStatement();
    public static final Statement<Object> PLAYER = new PlayerStatement();
    public static final Statement<Object> LIVING = new LivingStatement();
    public static final Statement<Object> ENTITY = new EntityStatement();
    public static final Statement<Object> EXPLOSION = new ExplosionStatement();
    public static final Statement<DataSerializable> TRANSACTION = new TransactionStatement();
    public static final Statement<Object> SERVER = new ServerStatement();
    public static final Statement<Object> EVENT = new EventStatement();
    public static final Statement<Object> SOURCE = new SourceStatement();

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
     * Gets an {@link Optional} {@link Statement} for the {@link Line}, such that {@link Statement.Syntax#matches(String)} is true
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

        return Optional.empty();
    }
}
