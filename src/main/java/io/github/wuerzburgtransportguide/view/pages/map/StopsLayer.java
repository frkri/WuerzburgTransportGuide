package io.github.wuerzburgtransportguide.view.pages.map;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;

import io.github.wuerzburgtransportguide.Util;
import io.github.wuerzburgtransportguide.model.GetJourneys200ResponseInnerLegsInnerStopSeqInner;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import org.controlsfx.control.PopOver;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// See:
// https://github.com/gluonhq/maps/blob/main/samples/src/main/java/com/gluonhq/maps/samples/PoiLayer.java
public class StopsLayer extends MapLayer {

    private final ArrayList<Pair<MapPoint, ImageView>> stopPoints = new ArrayList<>();

    public StopsLayer(
            List<GetJourneys200ResponseInnerLegsInnerStopSeqInner> stopSeq,
            boolean isOrigin,
            boolean isDestination) {
        super();

        try {
            var stopIconPinNormal = new Image(Util.getResource("assets/pin_drop.png").toString());
            var stopIconPinTransfer =
                    new Image(Util.getResource("assets/transfer_within_a_station.png").toString());
            var stopIconPinStart = new Image(Util.getResource("assets/trip_origin.png").toString());
            var stopIconPinEnd = new Image(Util.getResource("assets/location_on.png").toString());

            for (var i = 0; i < stopSeq.size(); i++) {
                var point = stopSeq.get(i);

                ImageView stopIconView;
                if (isOrigin && i == 0) stopIconView = new ImageView(stopIconPinStart);
                else if (isDestination && i == stopSeq.size() - 1)
                    stopIconView = new ImageView(stopIconPinEnd);
                else if (i == stopSeq.size() - 1) stopIconView = new ImageView(stopIconPinTransfer);
                else if (i == 0) continue;
                else stopIconView = new ImageView(stopIconPinNormal);

                var popover = createPopOver(point, i, stopSeq.size(), stopIconView);
                stopIconView.setOnMouseClicked(event -> popover.show(stopIconView));

                var coordinates = point.getRef().getCoords();
                add(
                        new MapPoint(coordinates.getLatitude(), coordinates.getLongitude()),
                        stopIconView);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void layoutLayer() {
        for (var pointPair : stopPoints) {
            var point = pointPair.getKey();
            var node = pointPair.getValue();

            var relativeMapPoint = getMapPoint(point.getLatitude(), point.getLongitude());
            node.setTranslateX(relativeMapPoint.getX() - 10);
            node.setTranslateY(relativeMapPoint.getY() - 10);
        }
    }

    private void add(MapPoint mapPoint, ImageView node) {
        var pointNode = new Pair<>(mapPoint, node);
        stopPoints.add(pointNode);

        this.getChildren().add(node);
        this.markDirty();
    }

    private PopOver createPopOver(
            GetJourneys200ResponseInnerLegsInnerStopSeqInner stop,
            int stopIndex,
            int stopCount,
            ImageView stopIconView)
            throws NullPointerException {
        var localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        var popOverContainer = new VBox();
        popOverContainer.setSpacing(5);
        popOverContainer.setPadding(new Insets(10));

        var header = new HBox();
        header.getChildren().add(stopIconView);

        var titleLabel = new Label(stop.getName());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        header.getChildren().add(titleLabel);
        popOverContainer.getChildren().add(header);

        if (stop.getPlace() != null) {
            var placeLabel = new Label("Place: " + stop.getPlace());
            popOverContainer.getChildren().add(placeLabel);
        }

        if (stop.getRef() != null && stop.getRef().getArrDateTime() != null) {
            var arrivalLabel =
                    new Label(
                            "Arrival: "
                                    + stop.getRef().getArrDateTime().format(localTimeFormatter));
            popOverContainer.getChildren().add(arrivalLabel);
        }

        if (stop.getRef().getDepDateTime() != null) {
            var departureLabel =
                    new Label(
                            "Departure: "
                                    + stop.getRef().getDepDateTime().format(localTimeFormatter));
            popOverContainer.getChildren().add(departureLabel);
        }

        var currentDelayLabel = new Label("Current delay: +" + stop.getRef().getArrDelay());
        popOverContainer.getChildren().add(currentDelayLabel);

        if (stop.getRef().getCoords() != null) {
            var coordsLabel =
                    new Label(
                            "Coordinates: "
                                    + stop.getRef().getCoords().getLongitude()
                                    + ", "
                                    + stop.getRef().getCoords().getLatitude());
            popOverContainer.getChildren().add(coordsLabel);
        }

        var popover = new PopOver(popOverContainer);
        popover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        popover.setTitle("Stop " + (stopIndex + 1) + " of " + stopCount);
        popover.setAutoHide(true);

        return popover;
    }
}
