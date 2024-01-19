package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import io.github.wuerzburgtransportguide.model.Coords;

import java.lang.reflect.Type;

public class CoordsSerializer implements JsonSerializer<Coords> {

    @Override
    public JsonElement serialize(Coords src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getLongitude() + "," + src.getLatitude());
    }
}
