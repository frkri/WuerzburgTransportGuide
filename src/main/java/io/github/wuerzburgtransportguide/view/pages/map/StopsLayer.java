package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;

import io.github.wuerzburgtransportguide.Util;
import io.github.wuerzburgtransportguide.model.GetJourneys200ResponseInnerLegsInnerStopSeqInner;

import javafx.scene.Node;
import javafx.scene.shape.SVGPath;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

// See:
// https://github.com/gluonhq/maps/blob/main/samples/src/main/java/com/gluonhq/maps/samples/PoiLayer.java
public class StopsLayer extends MapLayer {

    private final ArrayList<Pair<MapPoint, Node>> stopPoints = new ArrayList<>();

    public StopsLayer(List<GetJourneys200ResponseInnerLegsInnerStopSeqInner> stopSeq) {
        super();

        var svgContent = Util.getResource("assets/map-pin.svg");
        stopSeq.forEach(
                point -> {
                    var svgPath = new SVGPath();
                    svgPath.setContent(svgContent);
                    var coordinates = point.getRef().getCoords();
                    add(
                            new MapPoint(coordinates.getLatitude(), coordinates.getLongitude()),
                            svgPath);
                });
    }

    @Override
    protected void layoutLayer() {
        for (var pointPair : stopPoints) {
            var point = pointPair.getKey();
            var node = pointPair.getValue();

            var relativeMapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            node.setTranslateX(relativeMapPoint.getX());
            node.setTranslateY(relativeMapPoint.getY() - 10);
        }
    }

    private void add(MapPoint mapPoint, Node node) {
        var pointNode = new Pair<>(mapPoint, node);
        stopPoints.add(pointNode);

        this.getChildren().add(node);
        this.markDirty();
    }
}
