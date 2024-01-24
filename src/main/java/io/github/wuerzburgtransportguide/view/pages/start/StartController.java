package io.github.wuerzburgtransportguide.view.pages.start;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

import org.controlsfx.control.Notifications;

public class StartController extends ControllerHelper {
    public StartController(
            NetzplanApi apiService,
            SceneController sceneController,
            Notifications notificationBuilder) {
        super(apiService, sceneController, notificationBuilder);
    }

    public void switchSceneToRoute() {
        try {
            sceneController.navigateForward();
        } catch (IndexOutOfBoundsException e) {
            notificationBuilder
                    .title("Cannot navigate forwards")
                    .text("Route page not found")
                    .showError();
        }
    }
}
