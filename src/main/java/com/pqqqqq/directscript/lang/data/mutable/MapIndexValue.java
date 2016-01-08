package com.pqqqqq.directscript.lang.data.mutable;

import com.google.common.collect.Maps;
import com.pqqqqq.directscript.lang.data.Literal;

import java.util.Map;

/**
 * Created by Kevin on 2015-12-01.
 * <p>A {@link MutableValue} for a map entry.</p>
 * <p>A {@link DataHolder} containing a map must be given as it requires a field-membered variable.</p>
 */
public class MapIndexValue implements MutableValue<Literal<?>> {
    private final DataHolder mapHolder;
    private final Literal key;

    /**
     * Creates the {@link MapIndexValue} from the given {@link DataHolder} and key
     *
     * @param mapHolder the map holder
     * @param key       the {@link Literal} key
     */
    public MapIndexValue(DataHolder mapHolder, Literal key) {
        this.mapHolder = mapHolder;
        this.key = key;
    }

    /**
     * Gets the {@link DataHolder} corresponding to the map
     *
     * @return the data holder
     */
    public DataHolder getMapHolder() {
        return mapHolder;
    }

    /**
     * Gets the map key's {@link Literal}
     *
     * @return the datum
     */
    public Literal getKey() {
        return key;
    }

    @Override
    public Literal<?> getDatum() {
        Literal mapLiteral = getMapHolder().getDatum().tryLiteral();
        Map<Literal, Literal> map = Maps.newHashMap(mapLiteral.getMap());
        Literal value = map.get(getKey());

        if (value == null) {
            value = Literal.Literals.EMPTY;
            map.put(getKey(), value);
            getMapHolder().setDatum(Literal.fromObject(map)); // Update for addition
        }

        return value;
    }

    @Override
    public void setDatum(Literal<?> dataContainer) {
        Literal mapLiteral = getMapHolder().getDatum().tryLiteral();
        Map<Literal, Literal> map = Maps.newHashMap(mapLiteral.getMap());

        map.put(getKey(), dataContainer);
        getMapHolder().setDatum(Literal.fromObject(map)); // Update
    }
}
