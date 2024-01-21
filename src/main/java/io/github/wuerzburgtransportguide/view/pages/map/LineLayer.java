package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapLayer;

import io.github.wuerzburgtransportguide.model.CoordinatesList;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;

public class LineLayer extends MapLayer {

    private final CoordinatesList linePoints;
    private final Polyline polyline;
    private final boolean alternativeColorScheme;

    public LineLayer(CoordinatesList coordinatesList, boolean alternativeColorScheme) {
        super();

        this.polyline = new Polyline();
        this.linePoints = coordinatesList;
        this.alternativeColorScheme = alternativeColorScheme;

        polyline.setStrokeLineCap(StrokeLineCap.ROUND);
        polyline.setStrokeWidth(8D);

        this.getChildren().add(polyline);
        this.markDirty();
    }

    @Override
    protected void layoutLayer() {
        var coordinates = linePoints.getCoordinatesList();

        polyline.getPoints().clear();
        for (var point : coordinates) {
            var relativeMapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            polyline.getPoints().add(relativeMapPoint.getX());
            polyline.getPoints().add(relativeMapPoint.getY());
        }

        var firstPoint = coordinates.getFirst();
        var lastPoint = coordinates.getLast();

        var firstRelativePointMapPoint =
                getMapPoint(firstPoint.getLatitude(), firstPoint.getLongitude());
        var lastRelativePointMapPoint =
                getMapPoint(lastPoint.getLatitude(), lastPoint.getLongitude());

        LinearGradient lineGradient;
        if (alternativeColorScheme) {
            lineGradient =
                    new LinearGradient(
                            firstRelativePointMapPoint.getX(),
                            firstRelativePointMapPoint.getY(),
                            lastRelativePointMapPoint.getX(),
                            lastRelativePointMapPoint.getY(),
                            false,
                            CycleMethod.REFLECT,
                            new Stop(0, new Color(0.3, 0.5, 1, 0.7)),
                            new Stop(0.5, new Color(0.8, 0, 0.3, 0.7)));
        } else {
            lineGradient =
                    new LinearGradient(
                            firstRelativePointMapPoint.getX(),
                            firstRelativePointMapPoint.getY(),
                            lastRelativePointMapPoint.getX(),
                            lastRelativePointMapPoint.getY(),
                            false,
                            CycleMethod.REFLECT,
                            new Stop(0, new Color(0.1, 0.9, 0.2, 0.7)),
                            new Stop(0.5, new Color(0.9, 0.6, 0.1, 0.7)));
        }
        polyline.setStroke(lineGradient);
    }
}
