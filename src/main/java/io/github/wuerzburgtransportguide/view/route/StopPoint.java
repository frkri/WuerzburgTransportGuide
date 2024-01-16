package io.github.wuerzburgtransportguide.view.route;

import io.github.wuerzburgtransportguide.model.GetPlaces200ResponseInner;

public class StopPoint {
    public GetPlaces200ResponseInner stop;

    public StopPoint(GetPlaces200ResponseInner stop) {
        this.stop = stop;
    }

    @Override
    public String toString() {
        return stop.getTitle();
    }
}
