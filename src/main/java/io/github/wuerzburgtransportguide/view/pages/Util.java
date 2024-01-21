package io.github.wuerzburgtransportguide.view.pages;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Util {
    public static void visitSite(String url) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(url));
    }
}
