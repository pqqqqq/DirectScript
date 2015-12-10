package com.pqqqqq.directscript.lang.data.converter.entity;

import com.pqqqqq.directscript.lang.data.converter.Converter;
import com.pqqqqq.directscript.lang.data.converter.Converters;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-13.
 */
public class PlayerConverter extends Converter<Player> {
    public static final Map<Class, Function<Player, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        //CONVERSION_MAP.put(FoodData.class, Player::getFoodData);
        CONVERSION_MAP.put(String.class, Player::getName);
    }

    private PlayerConverter() {
        super(Player.class, CONVERSION_MAP, Converters.LIVING_CONVERTER);
    }

    public static PlayerConverter newInstance() {
        return new PlayerConverter();
    }
}
