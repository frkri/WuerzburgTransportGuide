package io.github.wuerzburgtransportguide.client.componenents;

import okhttp3.Headers;

import org.jetbrains.annotations.Nullable;

public class RequiredHeader {
    private final String headerName;
    private final IRequiredHeaderCallbacks headerCallbacks;

    public RequiredHeader(String headerName, IRequiredHeaderCallbacks headerCallbacks) {
        this.headerName = headerName;
        this.headerCallbacks = headerCallbacks;
    }

    public RequiredHeader(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerCallbacks =
                new IRequiredHeaderCallbacks() {
                    @Override
                    public String retrieveHeaderValue() {
                        return headerValue;
                    }

                    @Override
                    public boolean evaluateHeaders(Headers headers) {
                        return headers.get(headerName) == null;
                    }
                };
    }

    public @Nullable String processHeaders(Headers headers) {
        if (!headerCallbacks.evaluateHeaders(headers)) return null;
        return headerCallbacks.retrieveHeaderValue();
    }

    public String getHeaderName() {
        return headerName;
    }
}
