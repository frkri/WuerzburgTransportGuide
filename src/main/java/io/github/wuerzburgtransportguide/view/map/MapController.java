package io.github.wuerzburgtransportguide.view.map;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import io.github.wuerzburgtransportguide.view.Util;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class MapController {
    @FXML private Pane mapContainer;

    public void initialize() {
        var mapView = new MapView();
        mapView.setCenter(new MapPoint(49.783333, 9.933333));
        mapView.setZoom(13);

        mapView.addLayer(new LineLayer());
        mapContainer.getChildren().add(mapView);
    }

    public void visitOpenStreetMap() {
        Util.visitSite("https://www.openstreetmap.org/copyright");
    }

    public void visitVVM() {
        Util.visitSite("https://netzplan.vvm-info.de/");
    }
}
