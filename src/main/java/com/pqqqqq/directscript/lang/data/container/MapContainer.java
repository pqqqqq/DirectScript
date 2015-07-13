package com.pqqqqq.directscript.lang.data.container;

import com.pqqqqq.directscript.lang.data.Literal;
import com.pqqqqq.directscript.lang.data.LiteralHolder;
import com.pqqqqq.directscript.lang.script.ScriptInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 2015-07-11.
 * A map statement that is a {@link Map} of {@link DataContainer} K-V pairs
 */
public class MapContainer implements DataContainer {
    private final Map<DataContainer, DataContainer> map = new HashMap<DataContainer, DataContainer>();

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
     * Gets the {@link DataContainer} K-V map for this {@link MapContainer}
     *
     * @return the data container map
     */
    public Map<DataContainer, DataContainer> getMap() {
        return map;
    }

    @Override
    public Literal resolve(ScriptInstance scriptInstance) {
        Map<LiteralHolder, LiteralHolder> map = new HashMap<LiteralHolder, LiteralHolder>();
        for (Map.Entry<DataContainer, DataContainer> entry : getMap().entrySet()) {
            map.put(entry.getKey().resolve(scriptInstance).toHolder(), entry.getValue().resolve(scriptInstance).toHolder());
        }

        return Literal.fromObject(map);
    }
}
