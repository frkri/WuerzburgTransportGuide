module io.github.wuerzburgtransportguide {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires retrofit2;
    requires okhttp3;
    requires retrofit2.converter.gson;
    requires com.google.gson;
    requires jakarta.annotation;
    requires org.jetbrains.annotations;
    requires com.gluonhq.maps;
    requires java.desktop;
    requires org.apache.commons.text;
    requires org.openapitools.jackson.nullable;
    requires com.gluonhq.attach.storage;
    requires com.gluonhq.attach.util;

    opens io.github.wuerzburgtransportguide to
            javafx.fxml;
    opens io.github.wuerzburgtransportguide.model to
            com.google.gson;

    exports io.github.wuerzburgtransportguide;
    exports io.github.wuerzburgtransportguide.api;
    exports io.github.wuerzburgtransportguide.model;
    exports io.github.wuerzburgtransportguide.view.pages.map;
    exports io.github.wuerzburgtransportguide.view.pages.route;
    exports io.github.wuerzburgtransportguide.view.context;

    opens io.github.wuerzburgtransportguide.view.pages.map to
            javafx.fxml;
    opens io.github.wuerzburgtransportguide.view.pages.route to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.view.pages.start;

    opens io.github.wuerzburgtransportguide.view.pages.start to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.view.pages.route.availableRoutes;

    opens io.github.wuerzburgtransportguide.view.pages.route.availableRoutes to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.view.pages;

    opens io.github.wuerzburgtransportguide.view.pages to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.storage;

    opens io.github.wuerzburgtransportguide.storage to
            javafx.fxml;

    exports io.github.wuerzburgtransportguide.storage.cache;

    opens io.github.wuerzburgtransportguide.storage.cache to
            javafx.fxml;
}
