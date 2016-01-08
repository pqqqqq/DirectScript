package com.pqqqqq.directscript.lang.reader;

import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.exception.state.ExecutionException;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.generic.setters.BreakStatement;
import com.pqqqqq.directscript.lang.statement.generic.setters.ContinueStatement;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-14.
 * Represents a {@link List} of {@link Line}s that can be executed
 */
public class Block implements Iterable<Line> {
    private final int depthOffset;
    private final List<Line> lines = new ArrayList<>();

    /**
     * Creates an empty {@link Block} with the given depth offset
     *
     * @param depthOffset the depth offset, how far removed this block is from its origin
     */
    public Block(int depthOffset) {
        this.depthOffset = depthOffset;
    }

    /**
     * Creates a {@link Block} with the given {@link Line}s and depth offset
     *
     * @param depthOffset the depth offset, how far removed this block is from its origin
     * @param col the collection of lines
     */
    public Block(int depthOffset, Collection<? extends Line> col) {
        this(depthOffset);
        this.lines.addAll(col);
    }

    /**
     * <p>Gets the depth of this block, or how far removed this block is from the origin (main script block)</p>
     * <p>This value is an unsigned integer where 0 represents the main block, and anything above is an upper block</p>
     * @return the depth offset
     */
    public int getDepthOffset() {
        return depthOffset;
    }

    /**
     * Returns a {@link Set} of {@link Line}s that are a part of this Block
     *
     * @return a list of lines
     */
    public List<Line> getLines() {
        return lines;
    }

    /**
     * Converts this {@link Block} into a executable {@link Block.BlockRunnable} with the given {@link ScriptInstance}
     *
     * @param scriptInstance the script instance
     * @return the block runnable
     */
    public BlockRunnable toRunnable(ScriptInstance scriptInstance) {
        return new BlockRunnable(scriptInstance);
    }

    @Override
    public Iterator<Line> iterator() {
        return lines.iterator();
    }

    /**
     * The main {@link Runnable} {@link Environment} class for Blocks
     */
    public class BlockRunnable extends Environment implements Runnable {
        private final ScriptInstance scriptInstance;

        private Line currentLine = null;

        BlockRunnable(ScriptInstance scriptInstance) {
            super((scriptInstance.getCurrentRunnable().isPresent() ? scriptInstance.getCurrentRunnable().get() : scriptInstance));
            this.scriptInstance = scriptInstance;
        }

        /**
         * Gets the current {@link Line} in this runnable
         * @return the context
         */
        public Line getCurrentLine() {
            return currentLine;
        }

        /**
         * Executes the {@link Block} in the given {@link ScriptInstance} environment
         *
         * @return the {@link ScriptInstance.Result Result}
         */
        public ScriptInstance.Result execute() {
            Optional<BlockRunnable> oldBlockRunnable = checkNotNull(scriptInstance, "Script instance cannot be null").getCurrentRunnable();

            try {
                scriptInstance.setCurrentRunnable(Optional.of(this));
                for (Line line : Block.this) {
                    currentLine = line;
                    try {
                        if (scriptInstance.getReturnValue().isPresent()) {
                            return ScriptInstance.Result.SUCCESS; // Return if execution is halted
                        }

                        if (line.getDepthOffset() == getDepthOffset() && scriptInstance.getLinePredicate().apply(line)) {
                            Statement<?> statement = line.getStatement();

                            // Break and continue get special treatment
                            if (statement instanceof BreakStatement) {
                                return ScriptInstance.Result.FAILURE_BREAK;
                            }

                            if (statement instanceof ContinueStatement) {
                                return ScriptInstance.Result.FAILURE_CONTINUE;
                            }

                            Context ctx = line.toContext(scriptInstance);
                            ctx.run();
                            scriptInstance.getContextSet().add(ctx); // Add to context set
                        }
                    } catch (Throwable e) {
                        Lang.instance().exceptionHandler().log(new ExecutionException(e, "Error in script '%s' -> '%s' at line #%d (script line #%d): ", scriptInstance.getScript().getScriptsFile().getStringRepresentation(), scriptInstance.getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber()));
                        Lang.instance().exceptionHandler().flush();
                        return ScriptInstance.Result.FAILURE_ERROR; // Stop running of script
                    }
                }

                return ScriptInstance.Result.SUCCESS;
            } finally {
                scriptInstance.setCurrentRunnable(oldBlockRunnable);
            }
        }

        @Override
        public void run() {
            execute();
        }
    }
}
