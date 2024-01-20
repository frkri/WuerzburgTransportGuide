package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer implements JsonSerializer<OffsetDateTime> {
    @Override
    public JsonElement serialize(
            OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        var dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return new JsonPrimitive(src.format(dateTimeFormatter));
    }
}
