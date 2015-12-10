package com.pqqqqq.directscript.lang.data.converter;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kevin on 2015-11-15.
 */
public class TextConverter extends Converter<Text> {
    public static final Map<Class, Function<Text, ?>> CONVERSION_MAP = new HashMap<>();

    static {
        CONVERSION_MAP.put(String.class, (text) -> Texts.legacy('&').to(text));
    }

    private TextConverter() {
        super(Text.class, CONVERSION_MAP);
    }

    public static TextConverter newInstance() {
        return new TextConverter();
    }
}
