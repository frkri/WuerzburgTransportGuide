package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        // Example: 20240118 06:35
        // This does not conform to ISO 8601, so we have to parse it manually
        var formatter =
                new DateTimeFormatterBuilder().appendPattern("yyyyMMdd HH:mm").toFormatter();
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
