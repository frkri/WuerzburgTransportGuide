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
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import org.controlsfx.control.Notifications;

import java.util.NoSuchElementException;

public class MapController extends ControllerHelper implements IMapContext {

    @FXML private Pane mapContainer;
    @FXML private Label start;
    @FXML private Label destination;

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
        var legs = mapContext.journeys.getLegs();

        start.setText(mapContext.start.getName());
        destination.setText(mapContext.destination.getName());
        var mapView = new MapView();

        var coords = legs.getFirst().getStopSeq().getFirst().getRef().getCoords();
        var startCenter = new MapPoint(coords.getLatitude(), coords.getLongitude());

        mapView.setCenter(startCenter);
        mapView.setZoom(13);

        try {
            for (var i = 0; i < legs.size(); i++) {
                var leg = legs.get(i);

                var lineLayer = new LineLayer(leg.getPath(), i % 2 == 0);
                mapView.addLayer(lineLayer);

                StopsLayer stopsLayer;
                if (i == 0) {
                    stopsLayer = new StopsLayer(leg.getStopSeq(), true, (legs.size() == 1));
                } else if (i == legs.size() - 1) {
                    stopsLayer = new StopsLayer(leg.getStopSeq(), false, true);
                } else {
                    stopsLayer = new StopsLayer(leg.getStopSeq(), false, false);
                }

                mapView.addLayer(stopsLayer);
            }
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
