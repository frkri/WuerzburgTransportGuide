package io.github.wuerzburgtransportguide;

import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.client.ApiClient;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        var apiBuilder = new ApiClient("https://netzplan.vvm-info.de/api/");
        var netzplanService = apiBuilder.createClientService(NetzplanApi.class);

        var basePath = "pages/";
        var pages =
                List.of(
                        basePath + "start/start.fxml",
                        basePath + "route/route.fxml",
                        basePath + "map/map.fxml");
        List<Class<?>> contextClasses = List.of(MapContext.class);

        var sceneController = buildSceneController(stage, pages, netzplanService, contextClasses);
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
            List<Class<?>> contextClasses) {
        var sceneController = new SceneController(stage, pages, 800, 500);
        var injectorLoader =
                buildDependencyInjectorLoader(netzplanService, sceneController, contextClasses);
        sceneController.setDependencyInjectorLoader(injectorLoader);
        return sceneController;
    }

    @NotNull private static DependencyInjectorLoader buildDependencyInjectorLoader(
            NetzplanApi apiService,
            SceneController sceneController,
            List<Class<?>> optionalContextClasses) {
        Callback<Class<?>, Object> defaultControllerFactory =
                controllerClass -> {
                    try {
                        Object controller;
                        if (controllerClass.getSuperclass().equals(ControllerHelper.class)) {
                            // NOTE: Change the parameters to match the ControllerHelper.class
                            // constructor.
                            // Also change the args passed to the constructor, so it matches.
                            controller =
                                    controllerClass
                                            .getConstructor(
                                                    NetzplanApi.class, SceneController.class)
                                            .newInstance(apiService, sceneController);
                        } else {
                            controller = controllerClass.getConstructor().newInstance();
                        }

                        for (var optionalContextClass : optionalContextClasses) {
                            try {
                                var method =
                                        controllerClass.getMethod(
                                                "set" + optionalContextClass.getSimpleName(),
                                                optionalContextClass);
                                method.invoke(
                                        controller,
                                        optionalContextClass.getConstructor().newInstance());
                            } catch (Exception ignored) {
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
