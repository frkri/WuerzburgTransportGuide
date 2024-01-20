package io.github.wuerzburgtransportguide.view.pages;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Util {
    public static void visitSite(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            // TODO Show toast
        }
    }
}
