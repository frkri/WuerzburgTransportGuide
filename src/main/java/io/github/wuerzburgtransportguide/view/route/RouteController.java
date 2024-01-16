package io.github.wuerzburgtransportguide.view.route;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.ControllerHelper;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class RouteController extends ControllerHelper {

    private final ObservableList<StopPoint> startListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private final ObservableList<StopPoint> destinationListDataView =
            FXCollections.observableArrayList(new ArrayList<>());

    private StopPoint selectedStart;
    private StopPoint selectedDestination;

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

        searchButton.setOnAction(
                value -> {
                    showAvailableRoutes().showAndWait();
                });

        start.textProperty()
                .addListener(
                        new ChangeListener<String>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends String> observableValue,
                                    String s,
                                    String t1) {
                                try {
                                    var query = observableValue.getValue();
                                    if (query.length() <= 2) return;
                                    startListDataView.clear();

                                    // TODO async fetch
                                    updateListFromQuery(query, startListDataView);
                                } catch (IOException e) {
                                    // TODO Show toast error
                                    throw new RuntimeException(e);
                                }
                            }
                        });

        destination
                .textProperty()
                .addListener(
                        new ChangeListener<String>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends String> observableValue,
                                    String s,
                                    String t1) {
                                try {
                                    var query = observableValue.getValue();
                                    if (query.length() <= 2) return;
                                    destinationListDataView.clear();

                                    // TODO async fetch
                                    updateListFromQuery(query, destinationListDataView);
                                } catch (IOException e) {
                                    // TODO Show toast error
                                    throw new RuntimeException(e);
                                }
                            }
                        });

        startList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        new ChangeListener<StopPoint>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends StopPoint> observableValue,
                                    StopPoint stopPoint,
                                    StopPoint t1) {
                                selectedStart = observableValue.getValue();
                                if (selectedStart != null) start.setText(selectedStart.toString());
                            }
                        });

        destinationList
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        new ChangeListener<StopPoint>() {
                            @Override
                            public void changed(
                                    ObservableValue<? extends StopPoint> observableValue,
                                    StopPoint stopPoint,
                                    StopPoint t1) {
                                selectedDestination = observableValue.getValue();
                                if (selectedDestination != null)
                                    destination.setText(selectedDestination.toString());
                            }
                        });
    }

    public void updateListFromQuery(String query, ObservableList<StopPoint> listDataView)
            throws IOException {
        var request = apiService.getPlaces("de-de", query);
        var response = request.execute();
        if (!response.isSuccessful() || response.body() == null)
            throw new IOException("Query has failed");
        for (var item : response.body()) {
            var stopPoint = new StopPoint(item);
            listDataView.add(stopPoint);
        }
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
