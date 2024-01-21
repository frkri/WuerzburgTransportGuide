package io.github.wuerzburgtransportguide.cache;

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
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private HashMap<String, List<Poi>> stopPointCache = new HashMap<>();

    public StopPointCache(int threshold) {
        this.threshold = threshold;
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

    public void loadFromStorage(Path path) throws IOException, ClassNotFoundException {
        var reader = new ObjectInputStream(new FileInputStream(path.toFile()));
        stopPointCache = (HashMap<String, List<Poi>>) reader.readObject();
    }

    public void saveToStorage(Path path) throws IOException {
        try {
            Files.createFile(path);
        } catch (IOException ignored) {
        }
        var writer = new ObjectOutputStream(new FileOutputStream(path.toFile()));
        writer.writeObject(stopPointCache);
    }
}
