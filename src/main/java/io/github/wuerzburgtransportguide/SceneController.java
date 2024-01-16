package io.github.wuerzburgtransportguide;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class SceneController {

    private DependencyInjectorLoader dependencyInjectorLoader;
    private Stage primaryStage;
    private List<String> pagesPaths;

    private double defaultSceneWidth = 0;
    private double defaultSceneHeight = 0;
    private int currentIndex = 0;

    public SceneController(
            Stage primaryStage,
            List<String> pagesPaths,
            double defaultSceneWidth,
            double defaultSceneHeight) {
        this.primaryStage = primaryStage;
        this.pagesPaths = pagesPaths;
        this.defaultSceneWidth = defaultSceneWidth;
        this.defaultSceneHeight = defaultSceneHeight;
    }

    public void setDependencyInjectorLoader(DependencyInjectorLoader dependencyInjectorLoader) {
        this.dependencyInjectorLoader = dependencyInjectorLoader;
    }

    public void navigateBack() throws IndexOutOfBoundsException {
        if (currentIndex - 1 < 0)
            throw new IndexOutOfBoundsException(
                    "Unable to navigate backwards, would be outside of bounds");

        var loaded = dependencyInjectorLoader.load(pagesPaths.get(--currentIndex));
        primaryStage.setScene(new Scene(loaded, defaultSceneWidth, defaultSceneWidth));
    }

    public void navigateForward() throws IndexOutOfBoundsException {
        if (currentIndex + 1 > pagesPaths.size())
            throw new IndexOutOfBoundsException(
                    "Unable to navigate forwards, would be outside of bounds");

        var loaded = dependencyInjectorLoader.load(pagesPaths.get(++currentIndex));
        primaryStage.setScene(new Scene(loaded, defaultSceneWidth, defaultSceneWidth));
    }

    public void navigateTo(int index) throws IndexOutOfBoundsException {
        if (index < 0)
            throw new IndexOutOfBoundsException(
                    "Unable to navigate backwards, would be outside of bounds");
        if (index > pagesPaths.size())
            throw new IndexOutOfBoundsException(
                    "Unable to navigate forwards, would be outside of bounds");

        var loaded = dependencyInjectorLoader.load(pagesPaths.get(index));
        primaryStage.setScene(new Scene(loaded, defaultSceneWidth, defaultSceneWidth));
        currentIndex = index;
    }
}
