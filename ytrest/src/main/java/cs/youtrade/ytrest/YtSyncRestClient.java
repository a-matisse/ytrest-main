package cs.youtrade.ytrest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cs.youtrade.ytrest.gson.GsonConfig;
import cs.youtrade.ytrest.util.YtMultiMap;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;

@Log4j2
@Builder
public class YtSyncRestClient {

    private final String baseUrl;
    @Builder.Default
    private final CloseableHttpClient httpClient = HttpClients.createMinimal();
    @Builder.Default
    private final boolean validateBody = true;
    @Builder.Default
    private final Gson GSON = GsonConfig.createGson();
    @Builder.Default
    private final boolean alwaysParse = false;

    /**
     * Creates a new request builder for the API call.
     *
     * @param method   HTTP method (GET, POST, etc.)
     * @param endpoint API endpoint path
     * @return request builder instance
     */
    public YtSyncRequest.YtSyncRequestBuilder fetchFromApi(HttpMethod method, String endpoint) {
        return YtSyncRequest.builder(this, method, endpoint);
    }

    private <T> RestAnswer<T> fetchFromApi(YtSyncRequest req) {
        try {
            ClassicHttpRequest request = new YtHttpRequestBuilder()
                    .setValidateBody(validateBody)
                    .setMethod(req.getMethod())
                    .setBaseUrl(baseUrl)
                    .setEndpoint(req.getEndpoint())
                    .setRawEndpoint(req.isRawEndpoint())
                    .setHeaders(req.getHeaders())
                    .setParams(req.getParams())
                    .setBody(req.getBody())
                    .setGson(GSON)
                    .build();
            return execute(request, req.getType());
        } catch (IOException e) {
            log.error("Error while fetching from API", e);
            return RestAnswer.getErrorAns();
        }
    }

    private <T> RestAnswer<T> execute(ClassicHttpRequest request, Type type) throws IOException {
        return httpClient.execute(request, response -> {
            HttpEntity entity = response.getEntity();
            int statusCode = response.getCode();
            if (!(statusCode >= 200 && statusCode < 300) && !alwaysParse)
                return new RestAnswer<>(statusCode);

            try (InputStream stream = entity.getContent();
                 InputStreamReader reader = new InputStreamReader(stream)) {
                T ans = fromJson(reader, type);
                return new RestAnswer<>(statusCode, ans);
            }
        });
    }

    private <T> T executeUnsafe(YtSyncRequest req) {
        try {
            ClassicHttpRequest request = new YtHttpRequestBuilder()
                    .setValidateBody(validateBody)
                    .setMethod(req.getMethod())
                    .setBaseUrl(baseUrl)
                    .setEndpoint(req.getEndpoint())
                    .setRawEndpoint(req.isRawEndpoint())
                    .setHeaders(req.getHeaders())
                    .setParams(req.getParams())
                    .setBody(req.getBody())
                    .setGson(GSON)
                    .build();
            return executeUnsafe(request, TypeToken.get(req.getType()));
        } catch (IOException e) {
            log.error("Error executing request", e);
            throw new RuntimeException("Failed to execute request", e);
        }
    }

    private <T> T executeUnsafe(ClassicHttpRequest request, TypeToken<?> type) throws IOException {
        return httpClient.execute(request, response -> {
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            return fromJson(responseBody, type.getType());
        });
    }

    private <T> T fromJson(InputStreamReader inputStream, Type type) {
        return GSON.fromJson(inputStream, type);
    }

    private <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class YtSyncRequest {
        private YtSyncRestClient client;
        private HttpMethod method;
        private String endpoint;
        private boolean rawEndpoint;
        private YtMultiMap<String, String> headers;
        private YtMultiMap<String, String> params;
        private Object body;
        private Type type;

        /**
         * Executes the request and returns parsed response.
         *
         * @param <T> expected response type
         * @return RestAnswer with response data
         */
        public <T> RestAnswer<T> fetch() {
            return client.fetchFromApi(this);
        }

        public <T> T fetchUnsafe() {
            return client.executeUnsafe(this);
        }

        /**
         * Creates a builder with required fields.
         *
         * @param method   HTTP method (GET, POST, etc.)
         * @param endpoint API endpoint path
         * @return builder instance
         */
        public static YtSyncRequestBuilder builder(YtSyncRestClient client, HttpMethod method, String endpoint) {
            return new YtSyncRequestBuilder()
                    .client(client)
                    .method(method)
                    .endpoint(endpoint);
        }

        public static class YtSyncRequestBuilder {
            private YtSyncRequestBuilder client(YtSyncRestClient client) {
                this.client = client;
                return this;
            }

            private YtSyncRequestBuilder method(HttpMethod method) {
                this.method = method;
                return this;
            }

            private YtSyncRequestBuilder endpoint(String endpoint) {
                this.endpoint = endpoint;
                return this;
            }

            /**
             * Treats the endpoint as an already formed URI path and preserves
             * URI delimiters such as commas. Disabled by default.
             *
             * @param rawEndpoint true to preserve the raw endpoint syntax
             * @return this builder
             */
            public YtSyncRequestBuilder rawEndpoint(boolean rawEndpoint) {
                this.rawEndpoint = rawEndpoint;
                return this;
            }

            /**
             * Sets headers from a regular Map.
             *
             * @param headersMap map of header names to values
             * @return this builder
             */
            public YtSyncRequestBuilder headers(Map<String, String> headersMap) {
                if (headersMap != null && !headersMap.isEmpty())
                    this.headers = YtMultiMap.fromMap(headersMap);
                return this;
            }

            public YtSyncRequestBuilder headers(YtMultiMap<String, String> headers) {
                if (headers != null && !headers.isEmpty())
                    this.headers = headers;
                return this;
            }

            /**
             * Sets query parameters from a regular Map.
             *
             * @param paramsMap map of parameter names to values
             * @return this builder
             */
            public YtSyncRequestBuilder params(Map<String, String> paramsMap) {
                if (paramsMap != null && !paramsMap.isEmpty())
                    this.params = YtMultiMap.fromMap(paramsMap);
                return this;
            }

            public YtSyncRequestBuilder params(YtMultiMap<String, String> params) {
                if (params != null && !params.isEmpty())
                    this.params = params;
                return this;
            }
        }
    }
}
