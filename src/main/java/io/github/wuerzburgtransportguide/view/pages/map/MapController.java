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

import java.util.NoSuchElementException;
import java.util.Objects;

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
        var wuCenter = new MapPoint(49.783333, 9.933333);

        mapView.setCenter(wuCenter);
        mapView.setZoom(13);

        try {
            var firstLeg = Objects.requireNonNull(mapContext.journeys.getLegs().getFirst());
            mapView.addLayer(new LineLayer(firstLeg.getPath()));
            mapView.addLayer(new StopsLayer(Objects.requireNonNull(firstLeg.getStopSeq())));
        } catch (NullPointerException | NoSuchElementException e) {
            notificationBuilder
                    .title("No route selected")
                    .text("Please select a route")
                    .showError();
            try {
                sceneController.navigateBack();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
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
