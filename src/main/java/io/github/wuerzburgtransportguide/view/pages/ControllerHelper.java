package io.github.wuerzburgtransportguide.view.pages;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;

public class ControllerHelper {
    protected NetzplanApi netzplanService;
    protected SceneController sceneController;

    public ControllerHelper() {}

    public ControllerHelper(NetzplanApi netzplanService, SceneController sceneController) {
        this.netzplanService = netzplanService;
        this.sceneController = sceneController;
    }
}
