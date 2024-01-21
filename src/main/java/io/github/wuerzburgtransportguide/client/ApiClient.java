package io.github.wuerzburgtransportguide.client;

import com.google.gson.GsonBuilder;

import io.github.wuerzburgtransportguide.client.adapters.*;
import io.github.wuerzburgtransportguide.client.componenents.FilteredCookieStore;
import io.github.wuerzburgtransportguide.client.componenents.RequiredHeader;
import io.github.wuerzburgtransportguide.client.componenents.RequiredHeadersInterceptor;
import io.github.wuerzburgtransportguide.client.componenents.RequiredXsrfInterceptor;
import io.github.wuerzburgtransportguide.model.Coordinates;
import io.github.wuerzburgtransportguide.model.CoordinatesList;

import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

public class ApiClient {
    private final Retrofit retrofitClient;

    public ApiClient(String apiUrl) {
        var okHttpClient =
                new OkHttpClient.Builder().followRedirects(false).retryOnConnectionFailure(true);

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

        // GSON with adapter for vanilla and custom types
        var gson =
                new GsonBuilder()
                        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeSerializer())
                        .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeDeserializer())
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                        .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                        .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                        .registerTypeAdapter(Coordinates.class, new CoordinatesSerializer())
                        .registerTypeAdapter(Coordinates.class, new CoordinatesDeserializer())
                        .registerTypeAdapter(CoordinatesList.class, new CoordinatesListSerializer())
                        .registerTypeAdapter(
                                CoordinatesList.class, new CoordinatesListDeserializer())
                        .registerTypeAdapter(Locale.class, new LocaleSerializer())
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
