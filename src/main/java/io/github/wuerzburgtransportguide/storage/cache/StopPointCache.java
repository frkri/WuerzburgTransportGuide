package io.github.wuerzburgtransportguide.storage.cache;

import io.github.wuerzburgtransportguide.model.Poi;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class StopPointCache implements Serializable {

    private final int threshold;
    private final Path path;
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private HashMap<String, List<Poi>> stopPointCache = new HashMap<>();

    public StopPointCache(int threshold, Path path) {
        this.threshold = threshold;
        this.path = path;
    }

    @Nullable public List<Poi> get(String query) {
        var closestKey = getClosestKey(query.toLowerCase());
        return stopPointCache.get(closestKey);
    }

    private @Nullable String getClosestKey(String query) {
        var closestDistance = Integer.MAX_VALUE;
        String closestStop = null;

        for (String cachedStopKey : stopPointCache.keySet()) {
            int distance = levenshteinDistance.apply(cachedStopKey, query);
            if (distance >= closestDistance) continue;

            closestDistance = distance;
            closestStop = cachedStopKey;
        }

        return closestDistance > threshold ? null : closestStop;
    }

    public void put(String query, List<Poi> pois) {
        stopPointCache.put(query.toLowerCase(), pois);
    }

    @SuppressWarnings("unchecked")
    public void loadFromStorage() throws IOException, ClassNotFoundException {
        if (!Files.exists(path)) throw new FileNotFoundException();
        var reader = new ObjectInputStream(new FileInputStream(path.toFile()));
        var hashMapObject = reader.readObject();
        if (hashMapObject instanceof HashMap<?, ?>) {
            stopPointCache = (HashMap<String, List<Poi>>) hashMapObject;
        } else {
            throw new IOException("Invalid file format");
        }
        reader.close();
    }

    public void saveToStorage() throws IOException {
        var writer = new ObjectOutputStream(new FileOutputStream(path.toFile()));
        writer.writeObject(stopPointCache);
        writer.close();
    }

    public void clear() throws IOException {
        stopPointCache.clear();
        Files.deleteIfExists(path);
    }
}
