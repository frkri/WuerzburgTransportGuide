package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.github.wuerzburgtransportguide.model.Coordinates;
import io.github.wuerzburgtransportguide.model.CoordinatesList;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Adapter for a string containing a list of coordinates separated by a space. {@code
 * "9.79948,49.87022,9.79948,49.87022"}
 */
public class CoordinatesListDeserializer implements JsonDeserializer<CoordinatesList> {

    @Override
    public CoordinatesList deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        var coords = json.getAsString().split(" ");
        var coordsListData = new ArrayList<Coordinates>(coords.length);
        for (var coordinates : coords) {
            var coordinatesSplit = coordinates.split(",");
            coordsListData.add(
                    new Coordinates(
                            Double.parseDouble(coordinatesSplit[0]),
                            Double.parseDouble(coordinatesSplit[1])));
        }

        return new CoordinatesList(coordsListData);
    }
}
