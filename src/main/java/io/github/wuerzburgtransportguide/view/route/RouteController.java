package io.github.wuerzburgtransportguide.view.route;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.ControllerHelper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class RouteController extends ControllerHelper {

    private final int MIN_QUERY_LENGTH = 2;

    private final ObservableList<StopPoint> startListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ObservableList<StopPoint> destinationListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private StopPoint selectedStart;
    private StopPoint selectedDestination;
    private Boolean isInternalChange = false;

    @FXML private TextField start;
    @FXML private ListView<StopPoint> startList;
    @FXML private TextField destination;
    @FXML private ListView<StopPoint> destinationList;
    @FXML private Button searchButton;

    // TODO Implement rate limiting
    private int lastInputTimestamp = 0;

    public RouteController() {
        super();
    }

    public RouteController(NetzplanApi apiService, SceneController sceneController) {
        super(apiService, sceneController);
    }

    public void initialize() {
        startList.setItems(startListDataView);
        destinationList.setItems(destinationListDataView);

        searchButton.setOnAction(value -> showAvailableRoutes().showAndWait());

        start.textProperty()
                .addListener((observableValue, s, t1) -> handleQuery(t1, startListDataView));

        destination
                .textProperty()
                .addListener((observableValue, s, t1) -> handleQuery(t1, destinationListDataView));

        startList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observableValue, stopPoint, t1) -> {
                            if (t1 == null) return;
                            isInternalChange = true;
                            start.setText(t1.toString());
                            isInternalChange = false;
                        });

        destinationList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observableValue, stopPoint, t1) -> {
                            if (t1 == null) return;
                            isInternalChange = true;
                            destination.setText(t1.toString());
                            isInternalChange = false;
                        });
    }

    public void handleQuery(String query, ObservableList<StopPoint> listDataView) {
        if (query.length() <= MIN_QUERY_LENGTH || isInternalChange) return;

        listDataView.clear();
        try {
            updateListFromQuery(query, listDataView);
        } catch (RuntimeException e) {
            // TODO Show toast error
            throw new RuntimeException(e);
        }
    }

    public void updateListFromQuery(String query, ObservableList<StopPoint> listDataView)
            throws RuntimeException {
        CompletableFuture.runAsync(
                () -> {
                    try {
                        var request = apiService.getPlaces("de-de", query);
                        var response = request.execute();
                        if (!response.isSuccessful() || response.body() == null)
                            throw new RuntimeException("Query has failed");

                        Platform.runLater(
                                () -> {
                                    for (var item : response.body()) {
                                        var stopPoint = new StopPoint(item);
                                        listDataView.add(stopPoint);
                                    }
                                });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Stage showAvailableRoutes() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setWidth(200);
        stage.setHeight(200);
        stage.setX(500);
        stage.setY(500);

        Pane pane = new Pane();
        stage.setScene(pane.getScene());
        return stage;
    }
}
