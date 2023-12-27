package io.github.wuerzburgtransportguide.client;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * A simple cookie store that only stores accepted cookies and removes expired cookies.
 * Older cookies are overwritten by new ones.
 */
public class FilteredCookieStore implements CookieJar {
    private final List<String> acceptedCookieNames;
    private final HashMap<String, Cookie> cookieStore = new HashMap<>();

    public FilteredCookieStore(List<String> acceptedCookieNames) {
        this.acceptedCookieNames = acceptedCookieNames;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, List<Cookie> list) {
        for (var cookie : list)
            if (acceptedCookieNames.contains(cookie.name())) cookieStore.put(cookie.name(), cookie);
    }

    @Override
    public @NotNull List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        evaluateExpiredCookies();
        return cookieStore.values().stream().toList();
    }

    public void evaluateExpiredCookies() {
        cookieStore.values().removeIf(cookie -> cookie.expiresAt() < System.currentTimeMillis());
    }
}
