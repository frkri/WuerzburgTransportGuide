package io.github.wuerzburgtransportguide;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Util {
    public static @Nullable Path getCacheDir() {
        var os = System.getProperty("os.name").toLowerCase();
        Path path = null;

        if (os.contains("win")) {
            path = Path.of(System.getenv("TEMP"), "wuerzburgtransportguide");
        } else if (os.contains("linux")) {
            path = Path.of(System.getProperty("user.home"), ".cache", "wuerzburgtransportguide");
        } else if (os.contains("mac")) {
            path =
                    Path.of(
                            System.getProperty("user.home"),
                            "Library",
                            "Caches",
                            "wuerzburgtransportguide");
        }

        if (path != null) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                return null;
            }
        }

        return path;
    }

    public static void visitSite(String url) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(url));
    }

    public static URL getResource(String path) {
        return Util.class.getResource(path);
    }
}
