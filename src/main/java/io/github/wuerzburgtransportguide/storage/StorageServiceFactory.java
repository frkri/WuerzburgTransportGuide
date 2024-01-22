package io.github.wuerzburgtransportguide.storage;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.impl.ServiceFactory;

import jakarta.annotation.Nonnull;

import java.util.Optional;

public class StorageServiceFactory implements ServiceFactory<StorageService> {

    private final StorageService storageService;

    public StorageServiceFactory(@Nonnull StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Class<StorageService> getServiceType() {
        return StorageService.class;
    }

    @Override
    public Optional<StorageService> getInstance() {
        return Optional.of(storageService);
    }
}
