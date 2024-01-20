package io.github.wuerzburgtransportguide.view.pages.route;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.cache.StopPointCache;
import io.github.wuerzburgtransportguide.model.Poi;
import io.github.wuerzburgtransportguide.model.PoiType;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.*;

public class RouteController extends ControllerHelper implements IMapContext {

    private static final int MIN_QUERY_LENGTH = 2;
    private static final int QUERY_DELAY = 200;
    private static final int CACHE_MATCH_THRESHOLD = 3;

    private final ObservableList<Poi> startListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ObservableList<Poi> destinationListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ScheduledExecutorService executorService =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    private final StopPointCache stopPointCache = new StopPointCache(CACHE_MATCH_THRESHOLD);
    private Boolean isInternalChange = false;

    @FXML private TextField start;
    @FXML private ListView<Poi> startList;
    @FXML private TextField destination;
    @FXML private ListView<Poi> destinationList;

    private MapContext mapContext;

    public RouteController(NetzplanApi apiService, SceneController sceneController) {
        super(apiService, sceneController);
    }

    public void initialize() {

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

    private static ListCell<Poi> createCellCallback(ListView<Poi> listView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Poi item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        };
    }

    public void handleQuery(String query, ObservableList<Poi> listDataView) {
        if (isInternalChange) return;

        var stopPoints = stopPointCache.get(query);
        if (stopPoints != null) Platform.runLater(() -> listDataView.setAll(stopPoints));

        if (scheduledFuture != null && !scheduledFuture.isDone()) scheduledFuture.cancel(false);
        if (query.length() <= MIN_QUERY_LENGTH) return;
        scheduledFuture =
                executorService.schedule(
                        () -> {
                            try {
                                var request = netzplanService.getPlaces(Locale.getDefault(), query);
                                var response = request.execute();

                                if (response.code() == 500)
                                    throw new NotFoundException("Found no stops matching query");
                                if (!response.isSuccessful() || response.body() == null)
                                    throw new IOException("An error has occurred while querying");

                                var filteredStops =
                                        response.body().stream()
                                                .filter(poi -> poi.getType() == PoiType.STOP)
                                                .toList();

                                Platform.runLater(() -> listDataView.setAll(filteredStops));
                                stopPointCache.put(query, filteredStops);
                            } catch (IOException | NotFoundException e) {
                                // TODO Show Toast
                                e.printStackTrace();
                            }
                        },
                        QUERY_DELAY,
                        TimeUnit.MILLISECONDS);
    }

    public void showAvailableRoutes() {
        sceneController.showPopUp("pages/route/availableRoutes/availableRoutes.fxml");
    }

    @Override
    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }
}
