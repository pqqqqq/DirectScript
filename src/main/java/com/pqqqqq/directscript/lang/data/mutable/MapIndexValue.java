package com.pqqqqq.directscript.lang.data.mutable;

import com.google.common.collect.Maps;
import com.pqqqqq.directscript.lang.data.Datum;
import com.pqqqqq.directscript.lang.data.Literal;

import java.util.Map;

/**
 * Created by Kevin on 2015-12-01.
 * <p>A {@link MutableValue} for a map entry.</p>
 * <p>A {@link DataHolder} containing a map must be given as it requires a field-membered variable.</p>
 */
public class MapIndexValue implements MutableValue {
    private final DataHolder mapHolder;
    private final Datum key;

    /**
     * Creates the {@link MapIndexValue} from the given {@link DataHolder} and key
     *
     * @param mapHolder the map holder
     * @param key       the {@link Datum} key
     */
    public MapIndexValue(DataHolder mapHolder, Datum key) {
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
     * Gets the map key's {@link Datum}
     *
     * @return the datum
     */
    public Datum getKey() {
        return key;
    }

    @Override
    public <T> Datum<T> getDatum() {
        Literal mapLiteral = getMapHolder().getLiteral();
        Map<Datum, Datum> map = Maps.newHashMap(mapLiteral.getMap());
        Datum value = map.get(getKey());

        if (value == null) {
            value = Literal.Literals.EMPTY;
            map.put(getKey(), value);
            getMapHolder().setDatum(Literal.fromObject(map)); // Update for addition
        }

        return value;
    }

    @Override
    public void setDatum(Datum datum) {
        Literal mapLiteral = getMapHolder().getLiteral();
        Map<Datum, Datum> map = Maps.newHashMap(mapLiteral.getMap());

        map.put(getKey(), datum);
        getMapHolder().setDatum(Literal.fromObject(map)); // Update
    }
}
