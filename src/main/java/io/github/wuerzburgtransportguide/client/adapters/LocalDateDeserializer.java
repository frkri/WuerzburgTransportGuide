package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        // Example: 17.01.2024
        // This does not conform to ISO 8601, so we have to parse it manually
        var formatter = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyyy").toFormatter();
        return LocalDate.parse(json.getAsString(), formatter);
    }
}
