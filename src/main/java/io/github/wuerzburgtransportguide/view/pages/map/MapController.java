package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;
import io.github.wuerzburgtransportguide.view.pages.Util;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class MapController extends ControllerHelper implements IMapContext {

    @FXML private Pane mapContainer;
    private MapContext mapContext;

    public MapController() {
        super();
    }

    public MapController(NetzplanApi apiService, SceneController sceneController) {
        super(apiService, sceneController);
    }

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

    @Override
    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }
}
