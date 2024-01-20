package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import io.github.wuerzburgtransportguide.model.Coordinates;

import java.lang.reflect.Type;

/** Adapter for a string containing a pair of coordinates. {@code "9.79948,49.87022"} */
public class CoordinatesSerializer implements JsonSerializer<Coordinates> {

    @Override
    public JsonElement serialize(
            Coordinates src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getLongitude() + "," + src.getLatitude());
    }
}
