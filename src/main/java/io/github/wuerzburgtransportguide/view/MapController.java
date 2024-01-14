package io.github.wuerzburgtransportguide.view;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class MapController {

    @FXML public VBox main;

    public void initialize() {
        MapView mapView = new MapView();
        mapView.setCenter(new MapPoint(49.783333, 9.933333));
        mapView.setZoom(12);

        main.getChildren().add(mapView);
    }
}
