package io.github.wuerzburgtransportguide.client.componenents;

import okhttp3.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class RequiredXsrfInterceptor implements Interceptor {

    private final FilteredCookieStore cookieStore;
    private final Collection<String> requiredCookieNames;
    private final Collection<String> cookiePaths;
    private final OkHttpClient client;

    public RequiredXsrfInterceptor(
            FilteredCookieStore cookieStore, Collection<String> cookiePaths) {
        this.cookieStore = cookieStore;
        this.cookiePaths = cookiePaths;
        this.requiredCookieNames = List.of("XSRF-TOKEN", "laravel_token");
        client =
                new OkHttpClient.Builder()
                        .followRedirects(false)
                        .retryOnConnectionFailure(true)
                        .build();
    }

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        var outgoingRequestHeaders = chain.request().headers();
        var outGoingRequestBuilder = chain.request().newBuilder();

        cookieStore.evaluateExpiredCookies();
        if (!requiredCookieNames.stream()
                .allMatch(name -> cookieStore.getUnderlyingStore().containsKey(name))) {
            var requiredCookieValues = getNewCookies(outgoingRequestHeaders);
            if (requiredCookieValues == null) {
                chain.call().cancel();
                throw new IOException("Could not get required cookies");
            }
            requiredCookieValues.forEach(cookieStore::addCookie);
        }

        var xsrfTokenHeader = outgoingRequestHeaders.get("X-XSRF-TOKEN");
        if (xsrfTokenHeader == null) {
            var decodedXsrfTokenValue =
                    URLDecoder.decode(
                            cookieStore.getCookie("XSRF-TOKEN").value(), StandardCharsets.UTF_8);
            outGoingRequestBuilder.addHeader("X-XSRF-TOKEN", decodedXsrfTokenValue).build();
        }

        return chain.proceed(outGoingRequestBuilder.build());
    }

    private List<Cookie> getNewCookies(Headers headers) throws IOException {
        for (var path : cookiePaths) {
            var request = new Request.Builder().url(path).headers(headers).build();
            var response = client.newCall(request).execute();
            response.close();

            if (!response.isSuccessful()) continue;
            return Cookie.parseAll(request.url(), response.headers());
        }
        return null;
    }
}
