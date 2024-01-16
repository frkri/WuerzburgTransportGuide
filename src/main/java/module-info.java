module io.github.wuerzburgtransportguide {
    requires javafx.controls;
    requires javafx.fxml;
    requires retrofit2;
    requires okhttp3;
    requires retrofit2.converter.gson;
    requires com.google.gson;
    requires jakarta.annotation;
    requires org.jetbrains.annotations;
    requires com.gluonhq.maps;
    requires java.desktop;

    opens io.github.wuerzburgtransportguide to
            javafx.fxml;
    opens io.github.wuerzburgtransportguide.model to
            com.google.gson;

    exports io.github.wuerzburgtransportguide.api;
    exports io.github.wuerzburgtransportguide.model;
    exports io.github.wuerzburgtransportguide;
    exports io.github.wuerzburgtransportguide.view;

    opens io.github.wuerzburgtransportguide.view to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.view.map;

    opens io.github.wuerzburgtransportguide.view.map to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.view.route;

    opens io.github.wuerzburgtransportguide.view.route to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.view.start;

    opens io.github.wuerzburgtransportguide.view.start to
            javafx.fxml;
}
