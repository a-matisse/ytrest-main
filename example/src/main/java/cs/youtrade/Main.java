package cs.youtrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import cs.youtrade.ytrest.YtSyncRestClient;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClients;

@Slf4j
public class Main {
    private static final ObjectMapper jacksonMapper = new ObjectMapper();
    private static final YtSyncRestClient client = new YtSyncRestClient(
            "https://jsonplaceholder.typicode.com/",
            HttpClients.createDefault()
    );

    /**
     * This example uses GSON,
     * but you can use any library that returns {@link java.lang.reflect.Type}
     */
    public static void main(String[] args) {
        var ansGSON = fetchFromApiGSON();
        if (ansGSON.getStatus() != 200)
            log.error("Error fetching data from API: {}", ansGSON);
        else
            log.info(ansGSON.getResponse().toString());

        var ansFasterXML = fetchFromApiFasterXML();
        if (ansFasterXML.getStatus() != 200)
            log.error("Error fetching data from API: {}", ansFasterXML);
        else
            log.info(ansFasterXML.getResponse().toString());
    }

    private static RestAnswer<ExampleDto> fetchFromApiGSON() {
        return client.fetchFromApi(
                HttpMethod.GET,
                "todos/1",
                new TypeToken<ExampleDto>() {
                }.getType()
        );
    }

    private static RestAnswer<ExampleDto> fetchFromApiFasterXML() {
        var type = jacksonMapper
                .getTypeFactory()
                .constructType(ExampleDto.class)
                .getRawClass();
        return client.fetchFromApi(
                HttpMethod.GET,
                "todos/1",
                type
        );
    }

    @Data
    @ToString
    private static class ExampleDto {
        private long userId;
        private long id;
        private String title;
        private boolean completed;
    }
}