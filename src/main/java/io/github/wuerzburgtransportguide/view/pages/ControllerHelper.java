package io.github.wuerzburgtransportguide.view.pages;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;

import org.controlsfx.control.Notifications;

public class ControllerHelper {
    protected NetzplanApi netzplanService;
    protected SceneController sceneController;
    protected Notifications notificationBuilder;

    public ControllerHelper() {}

    public ControllerHelper(
            NetzplanApi netzplanService,
            SceneController sceneController,
            Notifications notificationBuilder) {
        this.netzplanService = netzplanService;
        this.sceneController = sceneController;
        this.notificationBuilder = notificationBuilder;
    }
}
