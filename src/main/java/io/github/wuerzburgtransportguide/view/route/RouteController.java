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

    private final ObservableList<StopPoint> startListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ObservableList<StopPoint> destinationListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ScheduledExecutorService executorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    private final HashMap<String, List<StopPoint>> stopPointCache = new HashMap<>();
    private Boolean isInternalChange = false;

    @FXML private TextField start;
    @FXML private ListView<StopPoint> startList;
    @FXML private TextField destination;
    @FXML private ListView<StopPoint> destinationList;
    @FXML private Button searchButton;

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
        if (isInternalChange) return;

        if (!updateDataViewFromCachedStopPoints(query, listDataView)) {
            updateDataViewFromStopPoints(query, listDataView);
        }
    }

    public boolean updateDataViewFromCachedStopPoints(
            String query, ObservableList<StopPoint> listDataView) {
        // See:
        // https://stackoverflow.com/questions/327513/fuzzy-string-search-library-in-java
        // https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/similarity/LevenshteinDistance.html
        String closestQuery = null;
        int closestDistance = Integer.MAX_VALUE;
        LevenshteinDistance ld = new LevenshteinDistance();

        for (String cachedQuery : stopPointCache.keySet()) {
            int distance = ld.apply(cachedQuery, query.toLowerCase());
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

    public void updateDataViewFromStopPoints(String query, ObservableList<StopPoint> listDataView) {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            // Make sure that the network previous query is canceled,
            // constraining the number of ongoing requests to 1
            scheduledFuture.cancel(true);
        }
        scheduledFuture =
                executorService.schedule(
                        () -> {
                            try {
                                var request = apiService.getPlaces("de-de", query);
                                var response = request.execute();
                                if (!response.isSuccessful() || response.body() == null)
                                    throw new RuntimeException("Query has failed");

                                Platform.runLater(
                                        () -> {
                                            var stopPoints = new ArrayList<StopPoint>();
                                            for (var item : response.body())
                                                stopPoints.add(new StopPoint(item));

                                            listDataView.clear();
                                            listDataView.addAll(stopPoints);
                                            stopPointCache.put(query.toLowerCase(), stopPoints);
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
