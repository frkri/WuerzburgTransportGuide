package io.github.wuerzburgtransportguide.client;

import org.jetbrains.annotations.Nullable;

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
}
