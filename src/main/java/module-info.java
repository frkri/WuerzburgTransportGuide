module io.github.wuerzburgtransportguide {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.github.wuerzburgtransportguide to javafx.fxml;
    exports io.github.wuerzburgtransportguide;
}