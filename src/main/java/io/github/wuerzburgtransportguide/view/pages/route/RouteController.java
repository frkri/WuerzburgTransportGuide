package io.github.wuerzburgtransportguide.view.pages.route;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.Util;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.model.Poi;
import io.github.wuerzburgtransportguide.model.PoiType;
import io.github.wuerzburgtransportguide.storage.cache.StopPointCache;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.*;

public class RouteController extends ControllerHelper implements IMapContext {

    private static final Duration QUERY_DELAY = Duration.millis(250);
    private static final int MIN_QUERY_LENGTH = 2;
    private static final int CACHE_MATCH_THRESHOLD = 2;

    private final ObservableList<Poi> startListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ObservableList<Poi> destinationListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final PauseTransition apiCallDebounce = new PauseTransition(QUERY_DELAY);
    private StopPointCache stopPointCache;
    private Boolean isInternalChange = false;

    @FXML private TextField start;
    @FXML private ListView<Poi> startList;
    @FXML private TextField destination;
    @FXML private ListView<Poi> destinationList;

    private MapContext mapContext;

    public RouteController(
            NetzplanApi apiService,
            SceneController sceneController,
            Notifications notificationBuilder) {
        super(apiService, sceneController, notificationBuilder);
    }

    public void initialize() {
        try {
            stopPointCache =
                    new StopPointCache(
                            CACHE_MATCH_THRESHOLD,
                            Objects.requireNonNull(
                                    Objects.requireNonNull(Util.getCacheDir())
                                            .resolve("stopPointCache.ser")));
        } catch (Exception e) {
            notificationBuilder
                    .title("Failed to create cache")
                    .text("Could not create cache, search performance may be affected")
                    .showError();
        }

        try {
            stopPointCache.loadFromStorage();
        } catch (IOException | ClassNotFoundException ignored) {
        }

        destinationList.setCellFactory(RouteController::createCellCallback);
        startList.setCellFactory(RouteController::createCellCallback);
        startList.setItems(startListDataView);
        destinationList.setItems(destinationListDataView);

        if (mapContext.start != null && mapContext.destination != null) {
            start.setText(mapContext.start.getName());
            destination.setText(mapContext.destination.getName());
        }
        start.textProperty().addListener(buildValueChangeListener(startListDataView));
        destination.textProperty().addListener(buildValueChangeListener(destinationListDataView));

        startList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observableValue, stopPoint, t1) -> {
                            if (t1 == null) return;
                            isInternalChange = true;
                            start.setText(t1.getName());
                            mapContext.start = t1;
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
                            mapContext.destination = t1;
                            isInternalChange = false;
                        });
    }

    private ChangeListener<String> buildValueChangeListener(ObservableList<Poi> listDataView) {
        return (observable, oldValue, query) -> {
            if (isInternalChange) return;
            var stopPoints = stopPointCache.get(query);
            if (stopPoints != null) {
                Platform.runLater(() -> listDataView.setAll(stopPoints));
                return;
            }

            if (query.length() <= MIN_QUERY_LENGTH) return;
            apiCallDebounce.setOnFinished(event -> handleQuery(query, listDataView));
            apiCallDebounce.playFromStart();
        };
    }

    private static ListCell<Poi> createCellCallback(ListView<Poi> listView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Poi item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        };
    }

    public void handleQuery(String query, ObservableList<Poi> listDataView) {
        // Get stop points from API
        executorService.submit(
                () -> {
                    try {
                        var request = netzplanService.getPlaces(Locale.getDefault(), query);
                        var response = request.execute();

                        if (response.code() == 500)
                            throw new NotFoundException("Could not find any stops matching query");
                        if (!response.isSuccessful() || response.body() == null)
                            throw new IOException("Could not fetch stops from server");

                        var filteredStops =
                                response.body().stream()
                                        .filter(poi -> poi.getType() == PoiType.STOP)
                                        .toList();
                        if (filteredStops.isEmpty()) return;

                        stopPointCache.put(query, filteredStops);
                        Platform.runLater(
                                () -> {
                                    if (start.getText() != null) listDataView.setAll(filteredStops);
                                });
                    } catch (IOException e) {
                        Platform.runLater(
                                () ->
                                        notificationBuilder
                                                .title("Network Error")
                                                .text("Could not fetch stops from server")
                                                .showError());
                    } catch (NotFoundException e) {
                        Platform.runLater(
                                () ->
                                        notificationBuilder
                                                .title("No stops found")
                                                .text(e.getMessage())
                                                .showWarning());
                    }
                },
                QUERY_DELAY);
    }

    public void showAvailableRoutes() {
        if (mapContext.start == null || mapContext.destination == null) {
            notificationBuilder
                    .title("Error in route query")
                    .text("No Start or Destination Selected.")
                    .showWarning();
            return;
        }
        if (mapContext.start.equals(mapContext.destination)) {
            notificationBuilder
                    .title("Error in route query")
                    .text("Start and Destination can not be the same.")
                    .showWarning();
            return;
        }

        try {
            stopPointCache.saveToStorage();
        } catch (IOException e) {
            Notifications.create()
                    .title("Failed to save cache")
                    .text("Could not save cache")
                    .showError();
        }
        sceneController.showModal("pages/route/availableRoutes/availableRoutes.fxml");
    }

    public void clearCache() {
        try {
            stopPointCache.clear();
        } catch (IOException e) {
            notificationBuilder
                    .title("Failed to clear cache")
                    .text("Could not clear cache")
                    .showError();
        }
        notificationBuilder
                .graphic(null)
                .title("Cache cleared")
                .text("Cache cleared successfully")
                .show();
    }

    @Override
    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }
}
