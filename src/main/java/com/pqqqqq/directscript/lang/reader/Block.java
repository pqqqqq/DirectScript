package com.pqqqqq.directscript.lang.reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kevin on 2015-06-14.
 * Represents a {@link List} of {@link Line}s that can be executed
 */
public class Block implements Iterable<Line> {
    private final List<Line> lines = new ArrayList<Line>();

    /**
     * Creates an empty {@link Block}
     */
    public Block() {
    }

    /**
     * Creates a {@link Block} with the given {@link Line}s
     *
     * @param col the collection of lines
     */
    public Block(Collection<? extends Line> col) {
        this.lines.addAll(col);
    }

    /**
     * Returns a {@link List} of {@link Line}s that are a part of this Block
     *
     * @return a list of lines
     */
    public List<Line> getLines() {
        return lines;
    }

    @Override
    public Iterator<Line> iterator() {
        return lines.iterator();
    }
}
