package io.github.wuerzburgtransportguide;

import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.client.ApiClient;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;

import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        var apiBuilder = new ApiClient("https://netzplan.vvm-info.de/api/");
        var netzplanService = apiBuilder.createClientService(NetzplanApi.class);
        var notificationBuilder =
                Notifications.create().hideAfter(Duration.seconds(3)).graphic(null);

        var basePath = "pages/";
        var pages =
                List.of(
                        basePath + "start/start.fxml",
                        basePath + "route/route.fxml",
                        basePath + "map/map.fxml");
        List<Pair<Class<?>, Object>> contextClasses =
                List.of(new Pair<>(IMapContext.class, new MapContext()));

        var sceneController =
                buildSceneController(
                        stage, pages, netzplanService, notificationBuilder, contextClasses);
        try {
            sceneController.navigateTo(0);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("Wuerzburg Transport Guide");
        stage.show();
    }

    @NotNull private static SceneController buildSceneController(
            Stage stage,
            List<String> pages,
            NetzplanApi netzplanService,
            Notifications notificationBuilder,
            List<Pair<Class<?>, Object>> contextClasses) {
        var sceneController = new SceneController(stage, pages, 800, 500);
        var injectorLoader =
                buildDependencyInjectorLoader(
                        netzplanService, sceneController, notificationBuilder, contextClasses);
        sceneController.setDependencyInjectorLoader(injectorLoader);
        return sceneController;
    }

    @NotNull private static DependencyInjectorLoader buildDependencyInjectorLoader(
            NetzplanApi apiService,
            SceneController sceneController,
            Notifications notificationBuilder,
            List<Pair<Class<?>, Object>> optionalContextClasses) {
        Callback<Class<?>, Object> defaultControllerFactory =
                controllerClass -> {
                    try {
                        Object controller;
                        if (controllerClass.getSuperclass().equals(ControllerHelper.class)) {
                            // NOTE: Change the parameters to match the ControllerHelper.class
                            // constructor.
                            // Also change the args passed to the constructor, so it matches and
                            // update module-info.java
                            controller =
                                    controllerClass
                                            .getConstructor(
                                                    NetzplanApi.class,
                                                    SceneController.class,
                                                    Notifications.class)
                                            .newInstance(
                                                    apiService,
                                                    sceneController,
                                                    notificationBuilder);
                        } else {
                            controller = controllerClass.getConstructor().newInstance();
                        }

                        for (var contextClass : optionalContextClasses) {
                            // Check if controller class implements one of the context classes
                            // through an interface
                            if (contextClass.getKey().isAssignableFrom(controllerClass)) {
                                contextClass
                                        .getKey()
                                        .getMethod(
                                                "set"
                                                        + contextClass
                                                                .getValue()
                                                                .getClass()
                                                                .getSimpleName(),
                                                contextClass.getValue().getClass())
                                        .invoke(controller, contextClass.getValue());
                            }
                        }

                        return controller;
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
