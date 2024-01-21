package io.github.wuerzburgtransportguide;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public final class Util {
    public static @Nullable Path getCacheDir() {
        var os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return Path.of(System.getenv("AppData"), "local", "wuerzburgtransportguide", "cache");
        } else if (os.contains("linux")) {
            return Path.of(System.getProperty("user.home"), ".cache", "wuerzburgtransportguide");
        } else if (os.contains("mac")) {
            return Path.of(
                    System.getProperty("user.home"),
                    "Library",
                    "Caches",
                    "wuerzburgtransportguide");
        }

        return null;
    }

    public static void visitSite(String url) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(url));
    }
}
