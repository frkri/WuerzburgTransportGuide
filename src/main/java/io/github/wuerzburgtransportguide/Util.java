package io.github.wuerzburgtransportguide;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Util {
    public static @Nullable Path getCacheDir() {
        var os = System.getProperty("os.name").toLowerCase();
        Path path = null;

        if (os.contains("win")) {
            path = Path.of(System.getenv("AppData"), "Local", "Temp", "wuerzburgtransportguide");
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

    public static String getResource(String path) {
        Util.class.getResource(path);
        var reader =
                new BufferedReader(
                        new InputStreamReader(
                                Objects.requireNonNull(Util.class.getResourceAsStream(path))));
        return reader.lines().collect(Collectors.joining());
    }
}
