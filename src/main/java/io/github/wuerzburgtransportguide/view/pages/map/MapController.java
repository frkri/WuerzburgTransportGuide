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
import javafx.stage.FileChooser;

import org.controlsfx.control.Notifications;

import java.io.FileWriter;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        mapView.setCenter(startCenter);
        mapView.setZoom(13);

        try {
            for (var i = 0; i < legs.size(); i++) {
                var leg = legs.get(i);

                var lineLayer = new LineLayer(leg.getPath(), i % 2 == 0);
                mapView.addLayer(lineLayer);

                StopsLayer stopsLayer;
                if (i == 0) {
                    stopsLayer =
                            new StopsLayer(
                                    netzplanService, leg.getStopSeq(), true, (legs.size() == 1));
                } else if (i == legs.size() - 1) {
                    stopsLayer = new StopsLayer(netzplanService, leg.getStopSeq(), false, true);
                } else {
                    stopsLayer = new StopsLayer(netzplanService, leg.getStopSeq(), false, false);
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

    public void printRoute() {
        FileWriter fileWriter;
        StringBuilder stringBuilder = new StringBuilder();
        FileChooser fileChooser = new FileChooser();

        try {
            var routePath = mapContext.journeys.getLegs().getFirst().getPoints();
            var legCount = mapContext.journeys.getLegs();

            // Header
            stringBuilder.append(
                    MessageFormat.format(
                            """
                            ┓ ┏┏┳┓┏┓
                            ┃┃┃ ┃ ┃┓
                            ┗┻┛ ┻ ┗┛

                            Route:
                            From: {0}
                            To: {1}

                            {2} {3}
                            {4} x Interchange
                            """,
                            routePath.getFirst().getName(),
                            mapContext.destination.getName(),
                            routePath.getFirst().getDateTime().getDate(),
                            routePath.getFirst().getDateTime().getTime(),
                            mapContext.journeys.getInterchange()));

            // Stops
            for (var leg : mapContext.journeys.getLegs()) {
                for (var stop : leg.getStopSeq()) {

                    stringBuilder.append(
                            MessageFormat.format(
                                    """
                            {0}     {1}
                            Delay: {2}
                            """,
                                    (stop.getRef().getDepDateTime() != null
                                            ? stop.getRef()
                                                    .getDepDateTime()
                                                    .format(DateTimeFormatter.ofPattern("HH:mm"))
                                            : stop.getRef()
                                                    .getArrDateTime()
                                                    .format(DateTimeFormatter.ofPattern("HH:mm"))),
                                    stop.getName(),
                                    stop.getRef().getArrDelay()));
                }
            }

            // TODO better filename, start - destination + dateTime
            // TODO set file location
            fileWriter = new FileWriter("test.txt");
            fileWriter.write(stringBuilder.toString());

            fileWriter.close();
        } catch (Exception e) {
            notificationBuilder.title("Cannot save file").text("Failed to save file").showError();
            e.printStackTrace();
        }
    }

    @Override
    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }
}
