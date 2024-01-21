package io.github.wuerzburgtransportguide.view.pages.route.availableRoutes;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.model.GetJourneys200ResponseInner;
import io.github.wuerzburgtransportguide.model.GetJourneysRequest;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class AvailableRoutesController extends ControllerHelper implements IMapContext {

    @FXML private VBox journeyBoxContainer;
    private MapContext mapContext;

    public AvailableRoutesController(
            NetzplanApi apiService,
            SceneController sceneController,
            Notifications notificationBuilder) {
        super(apiService, sceneController, notificationBuilder);
    }

    public void initialize() {
        List<GetJourneys200ResponseInner> journeys;
        try {
            var journeyRequest = new GetJourneysRequest();
            journeyRequest.setTime(OffsetDateTime.now());
            journeyRequest.setDate(OffsetDateTime.now());

            journeyRequest.setOrigin(mapContext.start);
            journeyRequest.setDestination(mapContext.destination);

            journeyRequest.setOptions(null);
            journeyRequest.setDeparture(1);
            var response =
                    netzplanService.getJourneys(journeyRequest, Locale.getDefault()).execute();
            journeys = response.body();

            if (!response.isSuccessful() || journeys == null || journeys.isEmpty()) {
                notificationBuilder
                        .title("No journeys found")
                        .text("Could not find any journeys. Check your query.")
                        .showError();
                return;
            }

            // Note: Needed due to javafx only creating the scene after initialize
            Platform.runLater(
                    () -> {
                        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

                        for (var i = 0; i != Math.min(4, journeys.size()); i++) {
                            var journey = journeys.get(i);
                            var journeyBox =
                                    (GridPane)
                                            sceneController.loadNode(
                                                    "pages/route/availableRoutes/journeyBox.fxml");

                            if (journey.getLegs() == null
                                    || journey.getLegs().isEmpty()
                                    || journey.getLegs().getFirst().getPoints() == null
                                    || journey.getLegs().getFirst().getPoints().isEmpty()
                                    || journey.getLegs().getLast().getPoints().isEmpty()) continue;
                            var departurePoint =
                                    journey.getLegs().getFirst().getPoints().getFirst();
                            var arrivalPoint = journey.getLegs().getLast().getPoints().getLast();

                            var date = (Label) journeyBox.lookup("#date");
                            var time = (Label) journeyBox.lookup("#time");
                            var interchange = (Label) journeyBox.lookup("#interchange");
                            var duration = (Label) journeyBox.lookup("#duration");
                            var arrival = (Label) journeyBox.lookup("#arrival");

                            date.setText(
                                    departurePoint.getDateTime().getDate().format(dateFormatter));
                            time.setText(departurePoint.getDateTime().getTime());
                            interchange.setText(journey.getInterchange() + "x");
                            duration.setText(journey.getDuration() + " h");
                            arrival.setText(arrivalPoint.getDateTime().getTime().toString());

                            journeyBox.setOnMouseClicked(
                                    mouseEvent -> {
                                        mapContext.journeys = journey;
                                        sceneController.getPrimaryModalStage().close();
                                        sceneController.navigateForward();
                                    });

                            journeyBoxContainer.getChildren().add(journeyBox);
                        }
                    });
        } catch (IOException e) {
            notificationBuilder
                    .title("Could not fetch journeys")
                    .text("An network error has occurred while loading journeys")
                    .showError();
        }
    }

    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }
}
