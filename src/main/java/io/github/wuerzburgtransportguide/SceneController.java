package io.github.wuerzburgtransportguide;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class SceneController {

    private DependencyInjectorLoader dependencyInjectorLoader;
    private final Stage primaryStage;
    private Stage primaryPopUpStage;
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

        navigateTo(--currentIndex);
    }

    public void navigateForward() throws IndexOutOfBoundsException {
        if (currentIndex + 1 > pagesPaths.size())
            throw new IndexOutOfBoundsException(
                    "Unable to navigate forwards, would be outside of bounds");

        navigateTo(++currentIndex);
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
        var loaded = dependencyInjectorLoader.load(path);
        var scenePopup = new Scene(loaded, defaultSceneWidth, defaultSceneHeight);

        primaryPopUpStage = new Stage();
        primaryPopUpStage.initModality(Modality.APPLICATION_MODAL);
        primaryPopUpStage.setScene(scenePopup);
        primaryPopUpStage.show();
    }

    public Stage getPrimaryPopUpStage() {
        return this.primaryPopUpStage;
    }

    public Parent loadNode(String path) {
        return dependencyInjectorLoader.load(path);
    }
}
