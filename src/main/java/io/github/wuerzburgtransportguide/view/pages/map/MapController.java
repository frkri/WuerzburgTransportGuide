package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.Util;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import org.controlsfx.control.Notifications;

public class MapController extends ControllerHelper implements IMapContext {

    @FXML private Pane mapContainer;
    private MapContext mapContext;

    public MapController() {
        super();
    }

    public MapController(
            NetzplanApi apiService,
            SceneController sceneController,
            Notifications notificationBuilder) {
        super(apiService, sceneController, notificationBuilder);
    }

    public void initialize() {
        var mapView = new MapView();
        mapView.setCenter(new MapPoint(49.783333, 9.933333));
        mapView.setZoom(13);

        mapView.addLayer(new LineLayer(mapContext.journeys.getLegs().getFirst().getPath()));
        mapView.addLayer(new StopsLayer(mapContext.journeys.getLegs().getFirst().getStopSeq()));
        mapContainer.getChildren().add(mapView);
    }

    public void visitOpenStreetMap() {
        try {

            Util.visitSite("https://www.openstreetmap.org/copyright");
        } catch (Exception e) {
            notificationBuilder.title("Cannot open site").text("Failed to open site").showError();
        }
    }

    public void visitVVM() {
        try {
            Util.visitSite("https://netzplan.vvm-info.de/");
        } catch (Exception e) {
            notificationBuilder.title("Cannot open site").text("Failed to open site").showError();
        }
    }

    @Override
    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }
}
