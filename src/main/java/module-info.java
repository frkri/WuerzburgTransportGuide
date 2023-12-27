module io.github.wuerzburgtransportguide {
    requires javafx.controls;
    requires javafx.fxml;

    // Dependencies for OpenAPI client
    requires retrofit2;
    requires okhttp3;
    requires com.google.gson;
    requires jakarta.annotation;
    requires org.jetbrains.annotations;

    opens io.github.wuerzburgtransportguide to javafx.fxml;
    exports io.github.wuerzburgtransportguide;
}