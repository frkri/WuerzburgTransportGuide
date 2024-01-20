package io.github.wuerzburgtransportguide;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class SceneController {

    private DependencyInjectorLoader dependencyInjectorLoader;
    private final Stage primaryStage;
    private final List<String> pagesPaths;

    private final double defaultSceneWidth;
    private final double defaultSceneHeight;
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
        primaryStage.setScene(new Scene(loaded, defaultSceneWidth, defaultSceneHeight));
    }

    public void navigateForward() throws IndexOutOfBoundsException {
        if (currentIndex + 1 > pagesPaths.size())
            throw new IndexOutOfBoundsException(
                    "Unable to navigate forwards, would be outside of bounds");

        var loaded = dependencyInjectorLoader.load(pagesPaths.get(++currentIndex));
        primaryStage.setScene(new Scene(loaded, defaultSceneWidth, defaultSceneHeight));
    }

    public void navigateTo(int index) throws IndexOutOfBoundsException {
        if (index < 0)
            throw new IndexOutOfBoundsException(
                    "Unable to navigate backwards, would be outside of bounds");
        if (index > pagesPaths.size())
            throw new IndexOutOfBoundsException(
                    "Unable to navigate forwards, would be outside of bounds");

        var loaded = dependencyInjectorLoader.load(pagesPaths.get(index));
        primaryStage.setScene(new Scene(loaded, defaultSceneWidth, defaultSceneHeight));
        currentIndex = index;
    }

    public void showPopUp(String path) {
        Stage popUpStage = new Stage();
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        var loaded = dependencyInjectorLoader.load(path);
        popUpStage.setScene(new Scene(loaded, defaultSceneWidth / 1.5, defaultSceneHeight / 1.5));
        popUpStage.show();
    }
}