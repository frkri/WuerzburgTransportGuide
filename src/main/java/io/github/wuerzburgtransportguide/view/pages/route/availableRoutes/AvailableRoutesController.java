package io.github.wuerzburgtransportguide.view.pages.route.availableRoutes;

import io.github.wuerzburgtransportguide.SceneController;
import io.github.wuerzburgtransportguide.api.NetzplanApi;
import io.github.wuerzburgtransportguide.view.context.IMapContext;
import io.github.wuerzburgtransportguide.view.context.MapContext;
import io.github.wuerzburgtransportguide.view.pages.ControllerHelper;

public class AvailableRoutesController extends ControllerHelper implements IMapContext {
    private MapContext mapContext;

    public AvailableRoutesController(NetzplanApi apiService, SceneController sceneController) {
        super(apiService, sceneController);
    }

    public void initialize() {
        // TODO implement
    }

    public void setMapContext(MapContext mapContext) {
        this.mapContext = mapContext;
    }
}
