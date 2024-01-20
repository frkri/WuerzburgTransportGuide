package io.github.wuerzburgtransportguide;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Arrays;

public class DependencyInjectorLoader {
    private final Callback<Class<?>, Object> controllerFactoryCallback;

    public DependencyInjectorLoader(Callback<Class<?>, Object> controllerFactoryCallback) {
        this.controllerFactoryCallback = controllerFactoryCallback;
    }

    public Parent load(String path) {
        try {
            var loader = new FXMLLoader(getClass().getResource(path));
            loader.setControllerFactory(controllerFactoryCallback);

            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
        }
    }
}
