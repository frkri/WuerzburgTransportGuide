package io.github.wuerzburgtransportguide.view.map;

import com.gluonhq.maps.MapPoint;

import javafx.scene.Node;

public class PointNode {

    public MapPoint mapPoint;
    public Node node;

    public PointNode(MapPoint mapPoint, Node node) {
        this.mapPoint = mapPoint;
        this.node = node;
    }
}
