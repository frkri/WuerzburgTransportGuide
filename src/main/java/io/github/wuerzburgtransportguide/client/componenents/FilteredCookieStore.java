package io.github.wuerzburgtransportguide.client.componenents;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple in-memory cookie store that only accepts cookies with matching names. Older cookies are
 * always overwritten by new ones and removed if expired.
 */
public class FilteredCookieStore implements CookieJar {
    private final List<String> acceptedCookieNames;
    private final HashMap<String, Cookie> cookieStore = new HashMap<>();

    public FilteredCookieStore(List<String> acceptedCookieNames) {
        this.acceptedCookieNames = acceptedCookieNames;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        for (var incomingCookie : list)
            if (acceptedCookieNames.contains(incomingCookie.name()))
                cookieStore.put(incomingCookie.name(), incomingCookie);
    }

    @Override
    public @NotNull List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        evaluateExpiredCookies();
        return new ArrayList<>(cookieStore.values());
    }

    public void evaluateExpiredCookies() {
        cookieStore.values().removeIf(cookie -> cookie.expiresAt() < System.currentTimeMillis());
    }

    public HashMap<String, Cookie> getUnderlyingStore() {
        return cookieStore;
    }

    public void addCookie(Cookie cookie) {
        if (acceptedCookieNames.contains(cookie.name())) cookieStore.put(cookie.name(), cookie);
    }

    public Cookie getCookie(String cookieName) {
        return cookieStore.get(cookieName);
    }
}
