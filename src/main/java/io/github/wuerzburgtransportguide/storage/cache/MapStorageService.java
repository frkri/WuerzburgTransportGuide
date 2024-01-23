package io.github.wuerzburgtransportguide.storage.cache;

import com.gluonhq.attach.storage.StorageService;

import io.github.wuerzburgtransportguide.Util;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class MapStorageService implements StorageService {
    @Override
    public Optional<File> getPrivateStorage() {
        return Optional.of(Objects.requireNonNull(Util.getCacheDir()).toFile());
    }

    @Override
    public Optional<File> getPublicStorage(String subdirectory) {
        return Optional.empty();
    }

    @Override
    public boolean isExternalStorageWritable() {
        return false;
    }

    @Override
    public boolean isExternalStorageReadable() {
        return false;
    }
}
