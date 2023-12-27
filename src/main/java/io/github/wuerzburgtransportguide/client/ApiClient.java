package io.github.wuerzburgtransportguide.client;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.nio.file.Path;
import java.util.List;

public class ApiClient {
    private final Retrofit retrofitClient;

    public ApiClient(String apiUrl) {
        var cacheDir = Path.of(System.getenv("AppData"), "local", "wuerzburgtransportguide", "cache").toFile();
        var cacheStore = new Cache(cacheDir, 10L * 1024L * 1024L);
        var cookieStore = new FilteredCookieStore(List.of("XSRF-TOKEN"));

        var okHttpClient = new OkHttpClient.Builder()
                .cache(cacheStore)
                .cookieJar(cookieStore)
                .followRedirects(false)
                .retryOnConnectionFailure(true)
                .build();
        retrofitClient = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(okHttpClient)
                .build();
    }

    public <T> T createClient(Class<T> clientInterface) {
        return retrofitClient.create(clientInterface);
    }
}