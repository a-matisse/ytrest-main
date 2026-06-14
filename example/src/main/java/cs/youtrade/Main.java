package cs.youtrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import cs.youtrade.ytrest.HttpMethod;
import cs.youtrade.ytrest.RestAnswer;
import cs.youtrade.ytrest.YtSyncRestClient;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class Main {
    private static final ObjectMapper jacksonMapper = new ObjectMapper();
    private static final YtSyncRestClient client = YtSyncRestClient
            .builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .build();

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
        return client
                .fetchFromApi(HttpMethod.GET, "todos/1")
                .type(getGsonType())
                .build()
                .fetch();
    }

    private static Type getGsonType() {
        return new TypeToken<ExampleDto>() {
        }.getType();
    }

    private static RestAnswer<ExampleDto> fetchFromApiFasterXML() {
        return client
                .fetchFromApi(HttpMethod.GET, "todos/1")
                .type(getJacksonType())
                .build()
                .fetch();
    }

    private static Type getJacksonType() {
        return jacksonMapper
                .getTypeFactory()
                .constructType(ExampleDto.class)
                .getRawClass();
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