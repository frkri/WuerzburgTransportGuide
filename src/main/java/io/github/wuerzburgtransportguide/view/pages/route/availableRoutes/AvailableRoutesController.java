package io.github.wuerzburgtransportguide.view.pages.route.availableRoutes;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

public class AvailableRoutesController extends ControllerHelper {

    public AvailableRoutesController(NetzplanApi apiService, SceneController sceneController) {
        super(apiService, sceneController);
    }
}
