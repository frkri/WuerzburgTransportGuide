package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import io.github.wuerzburgtransportguide.model.CoordinatesList;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Adapter for a string containing a list of coordinates separated by a comma. {@code
 * "9.79948,49.87022,9.79948,49.87022"}
 */
public class CoordinatesListSerializer implements JsonSerializer<CoordinatesList> {

    @Override
    public JsonElement serialize(
            CoordinatesList src, Type typeOfSrc, JsonSerializationContext context) {
        var coordsList = new ArrayList<String>(src.getCoordinatesList().size());
        // latitude, then longitude, because reasons I guess, sigh...
        for (var coordinates : src.getCoordinatesList())
            coordsList.add(coordinates.getLatitude() + "," + coordinates.getLongitude());

        return new JsonPrimitive(String.join(" ", coordsList));
    }
}
