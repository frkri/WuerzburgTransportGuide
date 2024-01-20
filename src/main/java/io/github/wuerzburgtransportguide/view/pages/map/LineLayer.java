package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;

// See:
// https://github.com/gluonhq/maps/blob/main/samples/src/main/java/com/gluonhq/maps/samples/PoiLayer.java
public class LineLayer extends MapLayer {

    private final ArrayList<Pair<MapPoint, Node>> linePoints;

    public LineLayer() {
        super();

        this.linePoints = new ArrayList<>();
        add(new MapPoint(49.783333, 9.933333), new Circle(15));
    }

    public LineLayer(ArrayList<Pair<MapPoint, Node>> linePoints) {
        super();

        this.linePoints = linePoints;
    }

    @Override
    protected void layoutLayer() {
        for (var pointPair : linePoints) {
            var point = pointPair.getKey();
            var node = pointPair.getValue();

            var relativeMapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            node.setTranslateX(relativeMapPoint.getX());
            node.setTranslateY(relativeMapPoint.getY());
        }
    }

    public void add(MapPoint mapPoint, Node node) {
        var pointNode = new Pair<>(mapPoint, node);
        linePoints.add(pointNode);

        this.getChildren().add(node);
        this.markDirty();
    }

    public void addAll(Collection<Pair<MapPoint, Node>> pairs) {
        for (var pair : pairs) {
            this.getChildren().add(pair.getValue());
        }

        linePoints.addAll(pairs);
        this.markDirty();
    }
}
