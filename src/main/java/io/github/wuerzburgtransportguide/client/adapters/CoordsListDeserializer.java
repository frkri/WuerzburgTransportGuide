package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.github.wuerzburgtransportguide.model.Coords;
import io.github.wuerzburgtransportguide.model.CoordsList;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CoordsListDeserializer implements JsonDeserializer<CoordsList> {

    @Override
    public CoordsList deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        var coords = json.getAsString().split(" ");
        var coordsListData = new ArrayList<Coords>(coords.length);
        for (var coord : coords) {
            var coordSplit = coord.split(",");
            coordsListData.add(
                    new Coords(
                            Double.parseDouble(coordSplit[0]), Double.parseDouble(coordSplit[1])));
        }

        return new CoordsList(coordsListData);
    }
}
