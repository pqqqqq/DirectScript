package com.pqqqqq.directscript.lang.reader;

import com.google.common.base.Optional;
import com.pqqqqq.directscript.lang.Lang;
import com.pqqqqq.directscript.lang.data.env.Environment;
import com.pqqqqq.directscript.lang.script.ScriptInstance;
import com.pqqqqq.directscript.lang.statement.Statement;
import com.pqqqqq.directscript.lang.statement.generic.setters.BreakStatement;
import com.pqqqqq.directscript.lang.statement.generic.setters.ContinueStatement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Kevin on 2015-06-14.
 * Represents a {@link List} of {@link Line}s that can be executed
 */
public class Block implements Iterable<Line> {
    private final int depthOffset;
    private final List<Line> lines = new ArrayList<Line>();

    /**
     * Creates an empty {@link Block} with the given depth offset
     *
     * @param depthOffset the depth offset
     */
    public Block(int depthOffset) {
        this.depthOffset = depthOffset;
    }

    /**
     * Creates a {@link Block} with the given {@link Line}s and depth offset
     *
     * @param depthOffset the depth offset
     * @param col the collection of lines
     */
    public Block(int depthOffset, Collection<? extends Line> col) {
        this(depthOffset);
        this.lines.addAll(col);
    }

    /**
     * Gets the depth offset of the block
     * @return the depth offset
     * @see Line#getDepth()
     */
    public int getDepthOffset() {
        return depthOffset;
    }

    /**
     * Returns a {@link List} of {@link Line}s that are a part of this Block
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
     * A class that acts as a {@link Runnable} for schedulers
     */
    public class BlockRunnable extends Environment implements Runnable {
        private final ScriptInstance scriptInstance;

        BlockRunnable(ScriptInstance scriptInstance) {
            super((scriptInstance.getCurrentRunnable().isPresent() ? scriptInstance.getCurrentRunnable().get() : scriptInstance));
            this.scriptInstance = scriptInstance;
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
                    try {
                        if (scriptInstance.getReturnValue().isPresent()) {
                            return ScriptInstance.Result.SUCCESS; // Return if execution is halted
                        }

                        if (line.getDepth() == getDepthOffset() && scriptInstance.getLinePredicate().apply(line)) {
                            Statement statement = line.getStatement();

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
                        Lang.instance().errorHandler().log(String.format("Error in script '%s' -> '%s' at line #%d (script line #%d): ", scriptInstance.getScript().getScriptsFile().getStringRepresentation(), scriptInstance.getScript().getName(), line.getAbsoluteNumber(), line.getScriptNumber()));
                        Lang.instance().errorHandler().log(e);
                        Lang.instance().errorHandler().flush();
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
