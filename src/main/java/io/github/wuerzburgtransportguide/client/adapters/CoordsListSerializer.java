package io.github.wuerzburgtransportguide.client.adapters;

import com.google.gson.*;

import io.github.wuerzburgtransportguide.model.CoordsList;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CoordsListSerializer implements JsonSerializer<CoordsList> {

    @Override
    public JsonElement serialize(CoordsList src, Type typeOfSrc, JsonSerializationContext context) {
        var coordsList = new ArrayList<String>(src.getCoordsList().size());
        for (var coord : src.getCoordsList())
            coordsList.add(coord.getLongitude() + "," + coord.getLatitude());

        return new JsonPrimitive(String.join(" ", coordsList));
    }
}
