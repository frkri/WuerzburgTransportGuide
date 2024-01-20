package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import io.github.wuerzburgtransportguide.model.Coordinates;

import java.lang.reflect.Type;

/** Adapter for a string containing a pair of coordinates. {@code "9.79948,49.87022"} */
public class CoordinatesDeserializer implements JsonDeserializer<Coordinates> {

    @Override
    public Coordinates deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        var coords = new Coordinates();
        var splitString = json.getAsString().split(",");
        coords.setLongitude(Double.parseDouble(splitString[0]));
        coords.setLatitude(Double.parseDouble(splitString[1]));
        return coords;
    }
}
