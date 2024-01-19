package io.github.wuerzburgtransportguide.model;

import java.util.ArrayList;

/** Pair of coordinates consisting of latitude and longitude. */
public class CoordsList {
    private final ArrayList<Coords> coords;

    public CoordsList() {
        coords = new ArrayList<>();
    }

    public CoordsList(ArrayList<Coords> coords) {
        this.coords = coords;
    }

    public ArrayList<Coords> getCoordsList() {
        return coords;
    }
}
