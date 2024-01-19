package io.github.wuerzburgtransportguide.view.route;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.model.GetPlaces200ResponseInner;
import io.github.wuerzburgtransportguide.view.ControllerHelper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class RouteController extends ControllerHelper {

    private static final int MIN_QUERY_LENGTH = 2;
    private static final int QUERY_DELAY = 200;
    private static final int LEVENSHTEIN_THRESHOLD = 3;

    private final ObservableList<GetPlaces200ResponseInner> startListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ObservableList<GetPlaces200ResponseInner> destinationListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ScheduledExecutorService executorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    private final HashMap<String, List<GetPlaces200ResponseInner>> stopPointCache = new HashMap<>();
    private Boolean isInternalChange = false;

    @FXML private TextField start;
    @FXML private ListView<GetPlaces200ResponseInner> startList;
    @FXML private TextField destination;
    @FXML private ListView<GetPlaces200ResponseInner> destinationList;
    @FXML private Button searchButton;

    public RouteController() {
        super();
    }

    public RouteController(NetzplanApi apiService, SceneController sceneController) {
        super(apiService, sceneController);
    }

    public void initialize() {
        searchButton.setOnAction(value -> showAvailableRoutes().showAndWait());

        destinationList.setCellFactory(RouteController::createCellCallback);
        startList.setCellFactory(RouteController::createCellCallback);
        startList.setItems(startListDataView);
        destinationList.setItems(destinationListDataView);

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
                            start.setText(t1.getName());
                            isInternalChange = false;
                        });

        destinationList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observableValue, stopPoint, t1) -> {
                            if (t1 == null) return;
                            isInternalChange = true;
                            destination.setText(t1.getName());
                            isInternalChange = false;
                        });
    }

    private static ListCell<GetPlaces200ResponseInner> createCellCallback(
            ListView<GetPlaces200ResponseInner> listView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(GetPlaces200ResponseInner item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        };
    }

    public void handleQuery(String query, ObservableList<GetPlaces200ResponseInner> listDataView) {
        if (isInternalChange) return;

        if (!updateDataViewFromCachedStopPoints(query, listDataView)) {
            updateDataViewFromStopPoints(query, listDataView);
        }
    }

    public boolean updateDataViewFromCachedStopPoints(
            String query, ObservableList<GetPlaces200ResponseInner> listDataView) {
        // See:
        // https://stackoverflow.com/questions/327513/fuzzy-string-search-library-in-java
        // https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/similarity/LevenshteinDistance.html
        String closestQuery = null;
        int closestDistance = Integer.MAX_VALUE;

        for (String cachedQuery : stopPointCache.keySet()) {
            int distance = new LevenshteinDistance().apply(cachedQuery, query.toLowerCase());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestQuery = cachedQuery;
            }
        }
        if ((closestDistance > LEVENSHTEIN_THRESHOLD || closestQuery == null)
                && query.length() >= MIN_QUERY_LENGTH) return false;

        var stopPoints = stopPointCache.get(closestQuery);
        if (stopPoints == null) return false;

        listDataView.clear();
        listDataView.addAll(stopPoints);
        return true;
    }

    public void updateDataViewFromStopPoints(
            String query, ObservableList<GetPlaces200ResponseInner> listDataView) {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            // Make sure that the previous network query is canceled,
            // constraining the number of ongoing requests to 1
            scheduledFuture.cancel(true);
        }
        scheduledFuture =
                executorService.schedule(
                        () -> {
                            try {
                                var request = netzplanService.getPlaces("de-de", query);
                                var response = request.execute();
                                if (!response.isSuccessful() || response.body() == null)
                                    throw new RuntimeException("Query has failed");

                                Platform.runLater(
                                        () -> {
                                            listDataView.clear();
                                            listDataView.addAll(response.body());
                                            stopPointCache.put(
                                                    query.toLowerCase(), response.body());
                                        });
                            } catch (IOException e) {
                                // TODO Show toast error
                                throw new RuntimeException(e);
                            }
                        },
                        QUERY_DELAY,
                        TimeUnit.MILLISECONDS);
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
