package io.github.wuerzburgtransportguide.client.componenents;

import okhttp3.Headers;

public interface IRequiredHeaderCallbacks {
    String retrieveHeaderValue();

    boolean evaluateHeaders(Headers headers);
}
