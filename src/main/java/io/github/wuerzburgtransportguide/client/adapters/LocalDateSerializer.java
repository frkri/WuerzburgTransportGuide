package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateSerializer implements JsonSerializer<LocalDate> {

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        // Example: 17.01.2024
        // This does not conform to ISO 8601, so we have to parse it manually
        var formatter = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy").toFormatter();
        return new JsonPrimitive(src.format(formatter));
    }
}
