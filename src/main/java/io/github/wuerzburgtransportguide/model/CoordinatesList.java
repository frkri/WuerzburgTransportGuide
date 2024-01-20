package io.github.wuerzburgtransportguide.model;

import java.util.ArrayList;

/** List of the {@link Coordinates} class */
public class CoordinatesList {
    private final ArrayList<Coordinates> coordinatesList;

    public CoordinatesList() {
        coordinatesList = new ArrayList<>();
    }

    public CoordinatesList(ArrayList<Coordinates> coords) {
        this.coordinatesList = coords;
    }

    public ArrayList<Coordinates> getCoordinatesList() {
        return coordinatesList;
    }
}
