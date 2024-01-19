package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import io.github.wuerzburgtransportguide.model.Coords;

import java.lang.reflect.Type;

public class CoordsDeserializer implements JsonDeserializer<Coords> {

    @Override
    public Coords deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        var coords = new Coords();
        var splitString = json.getAsString().split(",");
        coords.setLongitude(Double.parseDouble(splitString[0]));
        coords.setLatitude(Double.parseDouble(splitString[1]));
        return coords;
    }
}
