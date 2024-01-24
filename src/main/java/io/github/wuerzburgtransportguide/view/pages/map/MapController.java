package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.Util;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.model.GetJourneys200ResponseInnerLegsInner;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

public class MapController extends ControllerHelper implements IMapContext {

    @FXML private Pane mapContainer;
    @FXML private Label start;
    @FXML private Label destination;

    private MapContext mapContext;

    public MapController(
            NetzplanApi apiService,
            SceneController sceneController,
            Notifications notificationBuilder) {
        super(apiService, sceneController, notificationBuilder);
    }

    public void initialize() {
        var loadingImage = new Image(Util.getResource("assets/loading_bg.png").toString());
        var legs = mapContext.journeys.getLegs();
        var startStopPoint =
                new MapPoint(mapContext.start.getCentery(), mapContext.start.getCenterx());
        var endStopPoint =
                new MapPoint(
                        mapContext.destination.getCentery(), mapContext.destination.getCenterx());

        var mapView = new MapView();
        MapView.setPlaceholderImageSupplier(() -> loadingImage);
        mapView.setCenter(startStopPoint);
        mapView.setZoom(13);

        start.setText(mapContext.start.getName());
        destination.setText(mapContext.destination.getName());
        start.setOnMouseClicked(event -> mapView.flyTo(0D, startStopPoint, 1.5D));
        destination.setOnMouseClicked(event -> mapView.flyTo(0D, endStopPoint, 1.5D));

        try {
            for (var i = 0; i < legs.size(); i++) {
                var leg = legs.get(i);

                var isStartOrDestination = i == 0 || i == legs.size() - 1;
                var lineLayer = new LineLayer(leg.getPath(), isStartOrDestination);
                mapView.addLayer(lineLayer);

                StopsLayer stopsLayer = getStopsLayer(i, leg, legs);
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

    @NotNull private StopsLayer getStopsLayer(
            int i,
            GetJourneys200ResponseInnerLegsInner leg,
            List<GetJourneys200ResponseInnerLegsInner> legs) {
        StopsLayer stopsLayer;
        if (i == 0) {
            stopsLayer = new StopsLayer(leg.getStopSeq(), true, (legs.size() == 1));
        } else if (i == legs.size() - 1) {
            stopsLayer = new StopsLayer(leg.getStopSeq(), false, true);
        } else {
            stopsLayer = new StopsLayer(leg.getStopSeq(), false, false);
        }
        return stopsLayer;
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

    public void printRoute() {
        FileWriter fileWriter;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            var routePath = mapContext.journeys.getLegs().get(0).getPoints();
            var walks = 0;
            // Header
            stringBuilder.append(
                    MessageFormat.format(
                            """
                            ██     ██ ████████  ██████ \s
                            ██     ██    ██    ██      \s
                            ██  █  ██    ██    ██   ███\s
                            ██ ███ ██    ██    ██    ██\s
                             ███ ███     ██     ██████ \s


                            Route:
                            From: {0}
                            To: {1}

                            {2} {3}
                            {4} x Interchange

                            """,
                            routePath.get(0).getName(),
                            mapContext.destination.getName(),
                            routePath.get(0).getDateTime().getDate(),
                            routePath.get(0).getDateTime().getTime(),
                            mapContext.journeys.getInterchange()));

            // Stops
            for (var i = 0; mapContext.journeys.getLegs().size() > i; i++) {
                var leg = mapContext.journeys.getLegs().get(i);
                stringBuilder.append(
                        MessageFormat.format(
                                """

                                        {0}     {1}

                                        """,
                                leg.getMode().getName(), leg.getMode().getDestination()));

                for (var stop : leg.getStopSeq()) {
                    var ref = stop.getRef();

                    if (ref == null) continue;

                    stringBuilder.append(
                            MessageFormat.format(
                                    """
                                            |
                                            0 {0} {1}
                                            0 Delay:{2}
                                            |
                                            """,
                                    (ref.getDepDateTime() != null
                                            ? ref.getDepDateTime()
                                                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                                            : ""),
                                    stop.getName(),
                                    stop.getRef().getArrDelay()));
                }
                if (!leg.getMode().getProduct().equals("Fussweg")) {
                    stringBuilder.append(
                            MessageFormat.format(
                                    """
                                            
                                            ---
                                            {0}. Interchange

                                            """,
                                    i + 1 - walks));

                } else {
                    walks++;
                }
            }

            var file =
                    String.format(
                            "Route_"
                                    + mapContext.start.getName()
                                    + " - "
                                    + mapContext.destination.getName());
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(file);

            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");

            fileChooser.getExtensionFilters().add((extFilter));
            var result = fileChooser.showSaveDialog(null);
            if (result == null) return;

            fileWriter = new FileWriter(result.getPath(), StandardCharsets.UTF_8);
            fileWriter.write(stringBuilder.toString());

            fileWriter.close();
        } catch (Exception e) {
            notificationBuilder
                    .title("Cannot export file")
                    .text("Failed to export journey to file")
                    .showError();
        }
    }

    @Override
    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }

    public void navigateBack() {
        try {
            sceneController.navigateBack();
        } catch (IndexOutOfBoundsException e) {
            notificationBuilder
                    .title("Cannot navigate forwards")
                    .text("Route page not found")
                    .showError();
        }
    }
}
