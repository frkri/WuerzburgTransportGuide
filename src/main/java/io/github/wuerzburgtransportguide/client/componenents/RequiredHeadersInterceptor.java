package io.github.wuerzburgtransportguide.client.componenents;

import okhttp3.Interceptor;
import okhttp3.Response;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class RequiredHeadersInterceptor implements Interceptor {
    private final Collection<RequiredHeader> requiredHeaders;

    public RequiredHeadersInterceptor(Collection<RequiredHeader> requiredHeaders) {
        this.requiredHeaders = requiredHeaders;
    }

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        var outgoingRequest = chain.request();

        var newOutgoingRequest = outgoingRequest.newBuilder();
        var outgoingRequestHeaders = outgoingRequest.headers();
        requiredHeaders.parallelStream()
                .forEach(
                        requiredHeader -> {
                            var headerValue = requiredHeader.processHeaders(outgoingRequestHeaders);
                            if (headerValue == null) return;
                            newOutgoingRequest.addHeader(
                                    requiredHeader.getHeaderName(), headerValue);
                        });
        outgoingRequest = newOutgoingRequest.build();

        return chain.proceed(outgoingRequest);
    }
}
