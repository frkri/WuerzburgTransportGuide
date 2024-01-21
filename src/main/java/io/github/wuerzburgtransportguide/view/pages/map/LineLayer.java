package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapLayer;

import io.github.wuerzburgtransportguide.model.CoordinatesList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;

public class LineLayer extends MapLayer {

    private final CoordinatesList linePoints;
    private final Polyline polyline;

    public LineLayer(CoordinatesList coordinatesList) {
        super();

        this.polyline = new Polyline();
        this.linePoints = coordinatesList;

        polyline.setStrokeLineCap(StrokeLineCap.ROUND);
        polyline.setStroke(new Color(0.8, 0.0, 0.0, 0.5));
        polyline.setStrokeWidth(8.0);

        this.getChildren().add(polyline);
        this.markDirty();
    }

    @Override
    protected void layoutLayer() {
        polyline.getPoints().clear();
        for (var point : linePoints.getCoordinatesList()) {
            var relativeMapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            polyline.getPoints().add(relativeMapPoint.getX());
            polyline.getPoints().add(relativeMapPoint.getY());
        }
    }
}
