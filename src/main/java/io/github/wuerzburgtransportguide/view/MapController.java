package io.github.wuerzburgtransportguide.view;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class MapController {
    @FXML private Pane mapContainer;

    public void initialize() {
        var mapView = new MapView();
        mapView.setCenter(new MapPoint(49.783333, 9.933333));
        mapView.setZoom(14);

        mapContainer.getChildren().add(mapView);
    }

    public void visitOpenStreetMap() {
        Util.visitSite("https://www.openstreetmap.org/copyright");
    }

    public void visitVVM() {
        Util.visitSite("https://netzplan.vvm-info.de/");
    }
}
