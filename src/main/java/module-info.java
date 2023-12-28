module io.github.wuerzburgtransportguide {
  requires javafx.controls;
  requires javafx.fxml;
  requires retrofit2;
  requires okhttp3;
  requires com.google.gson;
  requires jakarta.annotation;

  opens io.github.wuerzburgtransportguide to
      javafx.fxml;

  exports io.github.wuerzburgtransportguide;
}
