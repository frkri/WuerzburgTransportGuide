package io.github.wuerzburgtransportguide;

import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.client.ApiClient;
import io.github.wuerzburgtransportguide.view.ControllerHelper;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        var apiBuilder = new ApiClient("https://netzplan.vvm-info.de/api/");
        var apiService = apiBuilder.createClientService(NetzplanApi.class);

        var basePath = "pages/";
        var pages =
                List.of(
                        basePath + "start/start.fxml",
                        basePath + "route/route.fxml",
                        basePath + "map/map.fxml");
        var sceneController = new SceneController(stage, pages, 800, 500);
        var injectorLoader = getDependencyInjectorLoader(apiService, sceneController);
        sceneController.setDependencyInjectorLoader(injectorLoader);

        try {
            sceneController.navigateTo(0);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Wuerzburg Transport Guide");
        stage.show();
    }

    @NotNull private static DependencyInjectorLoader getDependencyInjectorLoader(
            NetzplanApi apiService, SceneController sceneController) {
        Callback<Class<?>, Object> defaultControllerFactory =
                controllerClass -> {
                    try {
                        if (controllerClass.getSuperclass().equals(ControllerHelper.class)) {
                            return controllerClass
                                    .getConstructor(NetzplanApi.class, SceneController.class)
                                    .newInstance(apiService, sceneController);
                            // NOTE: Change the parameters to match the ControllerHelper.class
                            // constructor.
                            // Also change the args passed to the constructor, so it matches.
                        } else {
                            return controllerClass.getConstructor().newInstance();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
        return new DependencyInjectorLoader(defaultControllerFactory);
    }

    public static void main(String[] args) {
        launch();
    }
}
