module io.github.wuerzburgtransportguide {
    requires javafx.controls;
    requires javafx.fxml;
    requires retrofit2;
    requires okhttp3;
    requires retrofit2.converter.gson;
    requires com.google.gson;
    requires jakarta.annotation;
    requires org.jetbrains.annotations;

    opens io.github.wuerzburgtransportguide to
            javafx.fxml;
    opens io.github.wuerzburgtransportguide.model to
            com.google.gson;

    exports io.github.wuerzburgtransportguide;
}
