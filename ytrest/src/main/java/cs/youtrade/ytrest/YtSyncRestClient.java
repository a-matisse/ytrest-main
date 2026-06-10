package cs.youtrade.ytrest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cs.youtrade.ytrest.gson.GsonConfig;
import lombok.Builder;
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
import java.util.Collections;
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

    public void fetchFromApi(
            HttpMethod method, String endpoint
    ) {
        fetchFromApi(
                method, endpoint, Collections.emptyMap(), Collections.emptyMap(), null, new TypeToken<Void>() {
                }.getType());
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, Type type
    ) {
        return fetchFromApi(
                method, endpoint, Collections.emptyMap(), Collections.emptyMap(), null, type);
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, Object body, Type type
    ) {
        return fetchFromApi(
                method, endpoint, Collections.emptyMap(), Collections.emptyMap(), body, type);
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, Map<String, String> headers, Object body, Type type
    ) {
        return fetchFromApi(
                method, endpoint, headers, Collections.emptyMap(), body, type);
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, Map<String, String> headers, Map<String, String> params, Type type
    ) {
        return fetchFromApi(
                method, endpoint, headers, params, null, type);
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, Map<String, String> headers, Map<String, String> params, Object body, Type type
    ) {
        try {
            ClassicHttpRequest request = new YtHttpRequestBuilder()
                    .setValidateBody(validateBody)
                    .setMethod(method)
                    .setBaseUrl(baseUrl)
                    .setEndpoint(endpoint)
                    .setHeaders(headers)
                    .setParams(params)
                    .setBody(body)
                    .setGson(GSON)
                    .build();
            return execute(request, type);
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

    public <T> T executeUnsafe(HttpMethod method, String endpoint, Map<String, String> headers, Map<String, String> params, Object body, TypeToken<T> type) {
        try {
            ClassicHttpRequest request = new YtHttpRequestBuilder()
                    .setValidateBody(validateBody)
                    .setMethod(method)
                    .setBaseUrl(baseUrl)
                    .setEndpoint(endpoint)
                    .setHeaders(headers)
                    .setParams(params)
                    .setBody(body)
                    .setGson(GSON)
                    .build();
            return executeUnsafe(request, type);
        } catch (IOException e) {
            log.error("Error executing request", e);
            return null;
        }
    }

    private <T> T executeUnsafe(ClassicHttpRequest request, TypeToken<T> type) throws IOException {
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
}
