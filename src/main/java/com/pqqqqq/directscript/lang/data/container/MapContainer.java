package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.reader.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2015-07-11.
 * A map statement that is a {@link Map} of {@link DataContainer} K-V pairs
 */
public class MapContainer implements DataContainer<Map<Literal, Literal>> {
    private final Map<DataContainer, DataContainer> map = new HashMap<>();

    /**
     * Creates an empty {@link MapContainer}
     */
    public MapContainer() {
    }

    /**
     * Creates a new {@link MapContainer} with the given existing {@link Map}
     *
     * @param map the existing map
     */
    public MapContainer(Map<? extends DataContainer, ? extends DataContainer> map) {
        this.map.putAll(map);
    }

    /**
     * Gets the {@link Literal} K-V map for this {@link MapContainer}
     *
     * @return the data container map
     */
    public Map<DataContainer, DataContainer> getMap() {
        return map;
    }

    @Override
    public Literal<Map<Literal, Literal>> resolve(Context ctx) {
        Map<Literal, Literal> map = new HashMap<>();

        getMap().forEach((key, value) -> map.put(key.resolve(ctx), value.resolve(ctx)));

        return Literal.fromObject(map);
    }
}
