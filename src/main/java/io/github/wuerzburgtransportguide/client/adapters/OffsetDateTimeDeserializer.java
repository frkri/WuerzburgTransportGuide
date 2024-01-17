package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class OffsetDateTimeDeserializer implements JsonDeserializer<OffsetDateTime> {
    @Override
    public OffsetDateTime deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return OffsetDateTime.parse(json.getAsString());
        } catch (DateTimeParseException e) {
            throw new JsonParseException(e);
        }
    }
}
