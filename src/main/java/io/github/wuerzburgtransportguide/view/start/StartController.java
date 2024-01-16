package io.github.wuerzburgtransportguide.view.start;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.ControllerHelper;

public class StartController extends ControllerHelper {
    public StartController(NetzplanApi apiService, SceneController sceneController) {
        super(apiService, sceneController);
    }

    public void switchSceneToRoute() {
        try {
            sceneController.navigateForward();
        } catch (IndexOutOfBoundsException e) {
            // TODO Show toast
        }
    }
}
