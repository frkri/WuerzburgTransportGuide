package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    @Override
    public JsonElement serialize(
            LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // Example: 20240118 06:35
        // This does not conform to ISO 8601, so we have to parse it manually
        var formatter =
                new DateTimeFormatterBuilder().appendPattern("yyyyMMdd HH:mm").toFormatter();
        return new JsonPrimitive(src.format(formatter));
    }
}
