package io.github.wuerzburgtransportguide.view;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;

public class ControllerHelper {
    protected NetzplanApi apiService;
    protected SceneController sceneController;

    public ControllerHelper() {}

    public ControllerHelper(NetzplanApi apiService, SceneController sceneController) {
        this.apiService = apiService;
        this.sceneController = sceneController;
    }
}
