package io.github.wuerzburgtransportguide.client;

import com.google.gson.GsonBuilder;

import io.github.wuerzburgtransportguide.client.adapters.OffsetDateTimeDeserializer;
import io.github.wuerzburgtransportguide.client.adapters.OffsetDateTimeSerializer;
import io.github.wuerzburgtransportguide.client.componenents.FilteredCookieStore;
import io.github.wuerzburgtransportguide.client.componenents.RequiredHeader;
import io.github.wuerzburgtransportguide.client.componenents.RequiredHeadersInterceptor;
import io.github.wuerzburgtransportguide.client.componenents.RequiredXsrfInterceptor;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiClient {
    private final Retrofit retrofitClient;

    public ApiClient(String apiUrl) {
        var okHttpClient =
                new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(true);

        // Cache
        var cacheDir = Util.getCacheDir();
        if (cacheDir != null && cacheDir.toFile().canWrite() && cacheDir.toFile().canRead())
            okHttpClient.cache(new Cache(cacheDir.toFile(), 10L * 1024L * 1024L));

        // CookieStore
        var cookieStore = new FilteredCookieStore(List.of("XSRF-TOKEN", "laravel_token"));
        okHttpClient.cookieJar(cookieStore);

        // Required Headers
        var requiredHeaders =
                List.of(
                        new RequiredHeader("User-Agent", "WuerzburgTransportGuide/1.0.0"),
                        new RequiredHeader("Accept", "application/json"));
        okHttpClient.addInterceptor(new RequiredHeadersInterceptor(requiredHeaders));

        // XSRF Token Authenticator
        okHttpClient.addInterceptor(
                new RequiredXsrfInterceptor(cookieStore, List.of("https://netzplan.vvm-info.de/")));

        var gson =
                new GsonBuilder()
                        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeSerializer())
                        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeDeserializer())
                        .create();

        retrofitClient =
                new Retrofit.Builder()
                        .baseUrl(apiUrl)
                        .client(okHttpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
    }

    public <T> T createClientService(Class<T> ServiceClass) {
        return retrofitClient.create(ServiceClass);
    }
}
